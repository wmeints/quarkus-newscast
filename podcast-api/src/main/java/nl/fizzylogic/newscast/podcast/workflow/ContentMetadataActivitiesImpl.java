package nl.fizzylogic.newscast.podcast.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.azure.storage.blob.BlobServiceClient;

import jakarta.inject.Inject;
import nl.fizzylogic.newscast.podcast.clients.content.ContentClient;
import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.clients.content.model.CreatePodcastEpisode;
import nl.fizzylogic.newscast.podcast.clients.content.model.MarkAsProcessed;
import nl.fizzylogic.newscast.podcast.clients.content.model.MarkForProcessing;
import nl.fizzylogic.newscast.podcast.clients.content.model.PodcastEpisode;

public class ContentMetadataActivitiesImpl implements ContentMetadataActivities {
    @Inject
    ContentClient contentClient;

    @Inject
    BlobServiceClient blobServiceClient;

    @Override
    public void lockContentSubmissions(List<ContentSubmission> contentSubmissions) {
        for (var contentSubmission : contentSubmissions) {
            contentClient.markForProcessing(new MarkForProcessing(contentSubmission.id));
        }
    }

    @Override
    public void markContentSubmissionsAsProcessed(List<ContentSubmission> contentSubmissions) {
        for (var contentSubmission : contentSubmissions) {
            contentClient.markAsProcessed(new MarkAsProcessed(contentSubmission.id));
        }
    }

    @Override
    public PodcastEpisode savePodcastEpisode(String title, String audioFilePath,
            List<ContentSubmission> contentSubmissions) {
        var audioFile = new File(audioFilePath);
        var episodesContainer = blobServiceClient.getBlobContainerClient("episodes");

        episodesContainer.createIfNotExists();

        try (var inputStream = new FileInputStream(audioFile)) {
            var blob = episodesContainer.getBlobClient(audioFile.getName());
            blob.upload(inputStream);

            CreatePodcastEpisode createPodcastEpisodeInput = new CreatePodcastEpisode(
                    blob.getBlobName(), title,
                    buildShowNotes(contentSubmissions), buildDescription(contentSubmissions));

            var podcastEpisode = contentClient.createPodcastEpisode(createPodcastEpisodeInput);

            if (podcastEpisode.hasErrors()) {
                throw new RuntimeException("Failed to create podcast episode");
            }

            return podcastEpisode.get();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("Audio file not found: %s", audioFilePath), e);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error uploading audio file: %s", audioFilePath), e);
        }
    }

    private String buildShowNotes(List<ContentSubmission> contentSubmissions) {
        StringBuilder showNotes = new StringBuilder();
        showNotes.append("In this episode, we cover:\n\n");

        for (ContentSubmission submission : contentSubmissions) {
            if (submission.title != null && !submission.title.isEmpty()) {
                showNotes.append("• ").append(submission.title);
                if (submission.url != null && !submission.url.isEmpty()) {
                    showNotes.append(" (").append(submission.url).append(")");
                }
                showNotes.append("\n");
            }
        }

        return showNotes.toString();
    }

    private String buildDescription(List<ContentSubmission> contentSubmissions) {
        StringBuilder description = new StringBuilder();
        description.append("This episode covers the latest developments in technology and software engineering. ");
        description.append("We discuss ");

        if (!contentSubmissions.isEmpty()) {
            for (int i = 0; i < contentSubmissions.size(); i++) {
                ContentSubmission submission = contentSubmissions.get(i);
                if (submission.title != null && !submission.title.isEmpty()) {
                    if (i > 0 && i == contentSubmissions.size() - 1) {
                        description.append(" and ");
                    } else if (i > 0) {
                        description.append(", ");
                    }
                    description.append(submission.title.toLowerCase());
                }
            }
            description.append(".");
        }

        return description.toString();
    }
}
