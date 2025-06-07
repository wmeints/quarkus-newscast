package nl.fizzylogic.newscast.podcast.workflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.ElevenLabsClient;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.model.CreateSpeechRequest;
import nl.fizzylogic.newscast.podcast.model.PodcastFragment;
import nl.fizzylogic.newscast.podcast.model.PodcastHost;
import nl.fizzylogic.newscast.podcast.service.AudioConcatenation;

public class GeneratePodcastAudioActivitiesImpl implements GeneratePodcastAudioActivities {
    @Inject
    @RestClient
    ElevenLabsClient elevenLabsClient;

    @ConfigProperty(name = "newscast.locations.audio-files")
    String outputDirectoryPath;

    @Inject
    AudioConcatenation audioConcatenation;

    @Override
    @Transactional
    @ActivateRequestContext
    public String generateSpeech(PodcastFragment fragment) {
        var podcastHost = PodcastHost.findByName(fragment.host);

        var response = elevenLabsClient.createSpeech(
                podcastHost.voiceId,
                "mp3_44100_128",
                new CreateSpeechRequest(fragment.content));

        // Write response content to a file in the data directory
        File dataDir = new File(outputDirectoryPath);

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        String filename = String.format("%s.mp3", UUID.randomUUID());
        File outputFile = new File(dataDir, filename);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            response.readEntity(InputStream.class).transferTo(fos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write audio file", e);
        }

        return outputFile.getPath();
    }

    @Override
    public String concatenateAudioFragments(List<String> audioFragments) {
        String filename = String.format("%s.mp3", UUID.randomUUID());
        File dataDir = new File(outputDirectoryPath);
        File outputFile = new File(dataDir, filename);

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        audioConcatenation.concatenateAudioFiles(audioFragments, outputFile);

        return outputFile.getPath();
    }

    @Override
    public String mixPodcastEpisode(String contentAudioFile) {
        return "";
    }
}
