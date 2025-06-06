package nl.fizzylogic.newscast.podcast.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.ElevenLabsClient;
import nl.fizzylogic.newscast.podcast.model.PodcastEpisodeData;
import nl.fizzylogic.newscast.podcast.model.PodcastHost;

@QuarkusTest
public class GeneratePodcastAudioStepTest {
    @Inject
    GeneratePodcastAudioStep podcastAudioGenerator;

    @InjectMock
    ElevenLabsClient elevenLabsClient;

    @Test
    @Transactional
    public void canGeneratePodcastAudio() {
        // Setup test hosts with voice IDs
        PodcastHost.builder()
            .withIndex(0)
            .withName("Alice")
            .withVoiceId("voice-id-alice")
            .withStyleInstructions("Friendly and engaging")
            .build()
            .persist();

        PodcastHost.builder()
            .withIndex(1)
            .withName("Bob") 
            .withVoiceId("voice-id-bob")
            .withStyleInstructions("Professional and authoritative")
            .build()
            .persist();

        // Mock ElevenLabs API response
        Response mockResponse = Mockito.mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getEntity()).thenReturn(new ByteArrayInputStream("fake audio data".getBytes()));
        when(elevenLabsClient.createSpeech(anyString(), anyString(), any())).thenReturn(mockResponse);

        // Create test data with a simple script
        String testScript = """
        {
            "title": "Test Podcast",
            "sections": [
                {
                    "fragments": [
                        {
                            "host": "Alice",
                            "content": "This is a test content fragment from Alice."
                        },
                        {
                            "host": "Bob", 
                            "content": "This is a test content fragment from Bob."
                        }
                    ]
                }
            ]
        }
        """;

        var podcastData = PodcastEpisodeData.builder()
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(1))
            .withContentSubmissions(List.of(new ContentSubmission()))
            .build()
            .withPodcastScript(testScript);

        var response = podcastAudioGenerator.process(podcastData);

        assertNotNull(response);
        assertNotNull(response.parsedScript);
        assertEquals("Test Podcast", response.parsedScript.title);
        assertTrue(response.audioFiles.size() > 0, "Audio files should be generated");
    }

    @Test
    public void canHandleEmptyScript() {
        var podcastData = new PodcastEpisodeData();
        var response = podcastAudioGenerator.process(podcastData);
        
        assertNotNull(response);
        assertEquals(0, response.audioFiles.size());
    }
}
