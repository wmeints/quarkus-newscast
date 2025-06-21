package nl.fizzylogic.newscast.podcast.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface BuzzsproutActivities {
    @ActivityMethod
    String publishPodcastEpisode(int seasonNumber, int episodeNumber,
            String title, String description, String showNotes, String audioFileUrl);
}