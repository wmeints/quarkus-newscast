package nl.fizzylogic.newscast.podcast.workflow;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import nl.fizzylogic.newscast.podcast.clients.buzzsprout.BuzzsproutClient;
import nl.fizzylogic.newscast.podcast.clients.buzzsprout.model.CreateEpisodeRequest;
import nl.fizzylogic.newscast.podcast.clients.buzzsprout.model.CreateEpisodeResponse;

public class BuzzsproutActivitiesImpl implements BuzzsproutActivities {

    @Inject
    @RestClient
    BuzzsproutClient buzzsproutClient;

    @ConfigProperty(name = "buzzsprout.podcast-id")
    String podcastId;

    Logger logger = Logger.getLogger(BuzzsproutActivitiesImpl.class);

    @Override
    public String publishPodcastEpisode(int seasonNumber, int episodeNumber, String title, String description,
            String showNotes, String audioFileUrl) {
        logger.infof("Publishing podcast episode to Buzzsprout: %s", title);

        try {
            CreateEpisodeRequest request = new CreateEpisodeRequest(
                    seasonNumber, episodeNumber, title, description,
                    showNotes, audioFileUrl);

            CreateEpisodeResponse response = buzzsproutClient.createEpisode(podcastId, request);

            logger.infof("Successfully published episode to Buzzsprout with ID: %d", response.id);

            return response.id.toString();
        } catch (Exception e) {
            logger.errorf(e, "Failed to publish episode to Buzzsprout: %s", title);
            throw new RuntimeException("Failed to publish episode to Buzzsprout", e);
        }
    }
}