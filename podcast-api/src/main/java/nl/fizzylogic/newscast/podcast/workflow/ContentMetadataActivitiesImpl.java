package nl.fizzylogic.newscast.podcast.workflow;

import java.util.List;

import jakarta.inject.Inject;
import nl.fizzylogic.newscast.podcast.clients.content.ContentClient;
import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.clients.content.model.MarkAsProcessed;
import nl.fizzylogic.newscast.podcast.clients.content.model.MarkForProcessing;

public class ContentMetadataActivitiesImpl implements ContentMetadataActivities {
    @Inject
    ContentClient contentClient;

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
    public void savePodcastEpisode(String audioFilePath, List<ContentSubmission> contentSubmissions) {

    }
}
