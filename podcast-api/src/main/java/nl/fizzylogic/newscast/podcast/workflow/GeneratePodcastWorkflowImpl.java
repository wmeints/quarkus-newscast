package nl.fizzylogic.newscast.podcast.workflow;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.clients.content.model.PodcastEpisode;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;

public class GeneratePodcastWorkflowImpl implements GeneratePodcastWorkflow {
    private final ContentMetadataActivities contentMetadataActivities;
    private final GeneratePodcastScriptActivities podcastScriptGenerationActivities;
    private final GeneratePodcastAudioActivities generatePodcastAudioActivities;
    private final BuzzsproutActivities buzzsproutActivities;

    public GeneratePodcastWorkflowImpl() {
        var contentMetadataActivityOptions = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(30)).build();

        var podcastScriptGenerationRetryOptions = RetryOptions.newBuilder()
                .setMaximumAttempts(5)
                .setInitialInterval(Duration.ofSeconds(30))
                .build();

        var podcastAudioGenerationRetryOptions = RetryOptions.newBuilder()
                .setMaximumAttempts(3)
                .setInitialInterval(Duration.ofMinutes(1))
                .build();

        var podcastScriptGenerationActivityOptions = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(podcastScriptGenerationRetryOptions)
                .build();

        var podcastAudioGenerationActivityOptions = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(30))
                .setRetryOptions(podcastAudioGenerationRetryOptions)
                .build();

        var buzzsproutActivityOptions = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(3)
                        .setInitialInterval(Duration.ofSeconds(30))
                        .build())
                .build();

        contentMetadataActivities = Workflow.newActivityStub(
                ContentMetadataActivities.class,
                contentMetadataActivityOptions);

        podcastScriptGenerationActivities = Workflow.newActivityStub(
                GeneratePodcastScriptActivities.class,
                podcastScriptGenerationActivityOptions);

        generatePodcastAudioActivities = Workflow.newActivityStub(
                GeneratePodcastAudioActivities.class,
                podcastAudioGenerationActivityOptions);

        buzzsproutActivities = Workflow.newActivityStub(
                BuzzsproutActivities.class,
                buzzsproutActivityOptions);
    }

    @Override
    public void generatePodcast(GeneratePodcastWorkflowInput input) {
        // Lock the content submissions to prevent other workflows from picking them up.
        contentMetadataActivities.lockContentSubmissions(input.contentSubmissions);

        // Use Azure OpenAI to generate a podcast script based on the content
        // submissions. We'll use configured podcast host metadata for the style and
        // language patterns.
        PodcastScript script = podcastScriptGenerationActivities
                .generatePodcastScript(new GeneratePodcastScriptInput(input.contentSubmissions));

        // Collect all fragments from the script, skippping the intro and outro
        // sections. We'll use fixed audio fragments for the intro and outro.
        var processableFragments = script.sections.stream()
                // .skip(1).limit(script.sections.size() - 2)
                .flatMap(section -> section.fragments.stream())
                .collect(Collectors.toList());

        // Use elevenlabs to convert the text to speech for each fragment.
        // This will use the voice clone registered for each host.
        var generatedFragments = processableFragments.stream()
                .map(fragment -> generatePodcastAudioActivities.generateSpeech(fragment))
                .collect(Collectors.toList());

        // Concatenate the MP3 files into a single audio file.
        // We'll concatenate the intro, content, and outro sections in the next step.
        var contentAudioFile = generatePodcastAudioActivities.concatenateAudioFragments(generatedFragments);

        // Mix the intro, content, and outro sections into a single audio file.
        var finalAudioFile = generatePodcastAudioActivities.mixPodcastEpisode(contentAudioFile);

        // Save the podcast episode with the generated audio file and the included
        // content submissions.
        PodcastEpisode episodeMetadata = contentMetadataActivities.savePodcastEpisode(
                script.title,
                finalAudioFile,
                input.contentSubmissions);

        // Publish the podcast episode to Buzzsprout
        buzzsproutActivities.publishPodcastEpisode(
                1, episodeMetadata.episodeNumber,
                script.title,
                episodeMetadata.description,
                episodeMetadata.showNotes,
                episodeMetadata.audioFilePath);

        // Mark the content submissions as processed.
        // Other workflow triggers can't pick them up anymore.
        contentMetadataActivities.markContentSubmissionsAsProcessed(input.contentSubmissions);
    }

    @Override
    public PodcastGenerationStatus getStatus() {
        return new PodcastGenerationStatus();
    }
}
