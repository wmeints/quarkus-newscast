package nl.fizzylogic.newscast.podcast.workflow;

import java.util.List;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.clients.content.model.PodcastEpisode;

@ActivityInterface
public interface ContentMetadataActivities {
    @ActivityMethod
    void lockContentSubmissions(List<ContentSubmission> contentSubmissions);

    @ActivityMethod
    void markContentSubmissionsAsProcessed(List<ContentSubmission> contentSubmissions);

    @ActivityMethod
    PodcastEpisode savePodcastEpisode(String title, String audioFilePath, List<ContentSubmission> contentSubmissions);
}
