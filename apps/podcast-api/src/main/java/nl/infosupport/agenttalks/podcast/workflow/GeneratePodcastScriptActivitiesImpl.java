package nl.infosupport.agenttalks.podcast.workflow;

import java.util.List;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import nl.infosupport.agenttalks.podcast.clients.content.model.ContentSubmission;
import nl.infosupport.agenttalks.podcast.model.PodcastHost;
import nl.infosupport.agenttalks.podcast.model.PodcastScript;
import nl.infosupport.agenttalks.podcast.service.PodcastScriptGenerator;

public class GeneratePodcastScriptActivitiesImpl implements GeneratePodcastScriptActivities {

    @Inject
    PodcastScriptGenerator podcastScriptGenerator;

    @Override
    @Transactional
    @ActivateRequestContext
    public PodcastScript generatePodcastScript(GeneratePodcastScriptInput input) {
        var firstPodcastHost = PodcastHost.findByIndex(1);
        var secondPodcastHost = PodcastHost.findByIndex(2);

        return podcastScriptGenerator.generatePodcastScript(
                firstPodcastHost.name, secondPodcastHost.name,
                firstPodcastHost.styleInstructions, secondPodcastHost.styleInstructions,
                combineContentSubmissions(input.contentSubmissions));
    }

    private String combineContentSubmissions(List<ContentSubmission> contentSubmissions) {
        StringBuilder combinedContent = new StringBuilder();

        for (var contentSubmission : contentSubmissions) {
            combinedContent.append(String.format("%s\n\n", contentSubmission.title));
            combinedContent.append(String.format("%s\n\n", contentSubmission.summary));
            combinedContent.append(String.format("URL: %s\n\n", contentSubmission.url));
        }

        return combinedContent.toString();
    }
}
