package nl.fizzylogic.newscast.podcast.workflow;

import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.model.PodcastHost;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;
import nl.fizzylogic.newscast.podcast.service.PodcastScriptGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class GeneratePodcastScriptActivitiesImplTest {

    private PodcastScriptGenerator podcastScriptGenerator;
    private GeneratePodcastScriptActivitiesImpl activities;

    @BeforeEach
    void setUp() {
        podcastScriptGenerator = mock(PodcastScriptGenerator.class);
        activities = new GeneratePodcastScriptActivitiesImpl();
        activities.podcastScriptGenerator = podcastScriptGenerator;
    }

    @Test
    void testGeneratePodcastScript_CallsGeneratorWithCombinedContent() {
        PanacheMock.mock(PodcastHost.class);

        ContentSubmission cs1 = new ContentSubmission();
        cs1.title = "Title 1";
        cs1.summary = "Summary 1";
        cs1.url = "http://url1";

        ContentSubmission cs2 = new ContentSubmission();
        cs2.title = "Title 2";
        cs2.summary = "Summary 2";
        cs2.url = "http://url2";

        when(PodcastHost.findByIndex(1)).thenReturn(
                new PodcastHost("Joop Snijder", "", "", "test-1", 1));

        when(PodcastHost.findByIndex(2)).thenReturn(
                new PodcastHost("Willem Meints", "", "", "test-2", 2));

        GeneratePodcastScriptInput input = new GeneratePodcastScriptInput();
        input.contentSubmissions = List.of(cs1, cs2);

        var expectedScript = new PodcastScript();

        when(podcastScriptGenerator.generatePodcastScript(any(), any(), any(), any(), any()))
                .thenReturn(expectedScript);

        var result = activities.generatePodcastScript(input);

        assertSame(expectedScript, result);

        var combinedContentCaptor = ArgumentCaptor.forClass(String.class);

        verify(podcastScriptGenerator).generatePodcastScript(
                eq("Joop Snijder"), eq("Willem Meints"), eq(""), eq(""), combinedContentCaptor.capture());

        var combinedContent = combinedContentCaptor.getValue();

        assertTrue(combinedContent.contains("Title 1"));
        assertTrue(combinedContent.contains("Summary 2"));
        assertTrue(combinedContent.contains("URL: http://url2"));
    }
}
