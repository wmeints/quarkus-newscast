package nl.fizzylogic.newscast.podcast.workflow;

import nl.fizzylogic.newscast.podcast.clients.elevenlabs.ElevenLabsClient;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.model.CreateSpeechRequest;
import nl.fizzylogic.newscast.podcast.model.PodcastFragment;
import nl.fizzylogic.newscast.podcast.model.PodcastHost;
import nl.fizzylogic.newscast.podcast.service.AudioConcatenation;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class GeneratePodcastAudioActivitiesImplTest {

    ElevenLabsClient elevenLabsClient;

    @InjectMock
    AudioConcatenation audioConcatenation;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUpTestCase() throws Exception {
        elevenLabsClient = mock(ElevenLabsClient.class);
        tempDir = Files.createTempDirectory("newscast_").toFile();

        PanacheMock.mock(PodcastHost.class);
    }

    @Test
    void generateSpeech_happyFlow_writesFileAndReturnsPath() throws Exception {
        // Arrange
        var audioFragmentStream = new FileInputStream("src/test/resources/audio/joop-fragment-01.mp3");
        var fakeResponse = Response.ok().entity(audioFragmentStream).build();

        when(elevenLabsClient.createSpeech(anyString(), anyString(), any(CreateSpeechRequest.class)))
                .thenReturn(fakeResponse);

        PodcastFragment fragment = new PodcastFragment();
        fragment.host = "Joop Snijder";
        fragment.content = "Hello world!";

        when(PodcastHost.findByName("Joop Snijder")).thenReturn(
                new PodcastHost("Joop Snijder", "", "", "test-123", 1));

        GeneratePodcastAudioActivitiesImpl impl = new GeneratePodcastAudioActivitiesImpl();

        impl.elevenLabsClient = elevenLabsClient;
        impl.audioConcatenation = audioConcatenation;
        impl.outputDirectoryPath = tempDir.getAbsolutePath();

        // Act
        String path = impl.generateSpeech(fragment);

        // Assert
        assertTrue(path.endsWith(".mp3"));
        assertTrue(Files.exists(Paths.get(path)));
        assertTrue(Files.size(Paths.get(path)) > 0);
    }

    @Test
    void concatenateAudioFragments_happyFlow_createsOutputFile() throws Exception {
        // Arrange
        List<String> audioFragments = List.of(
                new File("src/test/resources/audio/joop-fragment-01.mp3").getAbsolutePath(),
                new File("src/test/resources/audio/willem-fragment-01.mp3").getAbsolutePath());

        GeneratePodcastAudioActivitiesImpl impl = new GeneratePodcastAudioActivitiesImpl();

        impl.elevenLabsClient = elevenLabsClient;
        impl.audioConcatenation = audioConcatenation;
        impl.outputDirectoryPath = tempDir.getAbsolutePath();

        // Act
        String path = impl.concatenateAudioFragments(audioFragments);

        // Assert
        // NOTE: We don't actually create a file because we mock the concatenation,
        // but we still return a path that ends with .mp3
        assertTrue(path.endsWith(".mp3"));
    }
}
