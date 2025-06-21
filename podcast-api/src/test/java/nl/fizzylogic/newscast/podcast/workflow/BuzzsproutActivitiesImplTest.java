package nl.fizzylogic.newscast.podcast.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import nl.fizzylogic.newscast.podcast.clients.buzzsprout.BuzzsproutClient;
import nl.fizzylogic.newscast.podcast.clients.buzzsprout.model.CreateEpisodeRequest;
import nl.fizzylogic.newscast.podcast.clients.buzzsprout.model.CreateEpisodeResponse;

@QuarkusTest
class BuzzsproutActivitiesImplTest {
    private BuzzsproutActivitiesImpl activities;
    private BuzzsproutClient buzzsproutClient;

    @BeforeEach
    void setUp() {
        buzzsproutClient = mock(BuzzsproutClient.class);
        activities = new BuzzsproutActivitiesImpl();
        activities.buzzsproutClient = buzzsproutClient;
        activities.podcastId = "test-podcast-id";
    }

    @Test
    void testPublishPodcastEpisodeSuccess() {
        // Setup
        String title = "Test Episode";
        String description = "Test Description";
        String showNotes = "Test Show Notes";
        String audioFileUrl = "https://test.blob.core.windows.net/episodes/test.mp3";

        CreateEpisodeResponse mockResponse = new CreateEpisodeResponse();
        mockResponse.id = 12345L;
        mockResponse.title = title;
        mockResponse.publicUrl = "https://buzzsprout.com/test/12345";

        when(buzzsproutClient.createEpisode(eq("test-podcast-id"), any(CreateEpisodeRequest.class)))
                .thenReturn(mockResponse);

        // Execute
        String result = activities.publishPodcastEpisode(
                1, 1, title, description,
                showNotes, audioFileUrl);

        // Verify
        assertEquals("12345", result);
    }

    @Test
    void testPublishPodcastEpisodeFailure() {
        // Setup
        String title = "Test Episode";
        String description = "Test Description";
        String showNotes = "Test Show Notes";
        String audioFileUrl = "https://test.blob.core.windows.net/episodes/test.mp3";

        when(buzzsproutClient.createEpisode(eq("test-podcast-id"), any(CreateEpisodeRequest.class)))
                .thenThrow(new RuntimeException("Buzzsprout API call failed"));

        // Execute & Verify
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activities.publishPodcastEpisode(
                        1, 1, title, description, showNotes, audioFileUrl));

        assertEquals("Failed to publish episode to Buzzsprout", exception.getMessage());
    }
}