package nl.infosupport.agenttalks.content.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import nl.infosupport.agenttalks.content.eventbus.EventPublisher;
import nl.infosupport.agenttalks.content.graph.input.CreatePodcastEpisode;
import nl.infosupport.agenttalks.content.graph.input.MarkAsProcessed;
import nl.infosupport.agenttalks.content.graph.input.MarkForProcessing;
import nl.infosupport.agenttalks.content.graph.input.SubmitContent;
import nl.infosupport.agenttalks.content.graph.input.SummarizeContent;
import nl.infosupport.agenttalks.content.model.ContentSubmission;
import nl.infosupport.agenttalks.content.model.PodcastEpisode;
import nl.infosupport.agenttalks.content.model.SubmissionStatus;

@QuarkusTest
public class ContentGraphTest {
    @Inject
    private ContentGraph contentGraph;

    @InjectMock
    private EventPublisher eventPublisher;

    @Test
    @Transactional
    public void canSubmitContent() {
        // Act
        ContentSubmission result = contentGraph.submitContent(new SubmitContent("https://example.com/article"));

        // Assert
        assertNotNull(result);
        assertEquals("https://example.com/article", result.url);
        assertEquals(SubmissionStatus.SUBMITTED, result.status);
        assertNotNull(result.dateCreated);
        verify(eventPublisher).publishContentSubmissionCreated(any());
    }

    @Test
    @Transactional
    public void canFindSubmissions() {
        // Arrange - Create test submissions
        ContentSubmission submission1 = TestObjectFactory.createSubmittedContentSubmission();
        ContentSubmission submission2 = TestObjectFactory.createSummarizedContentSubmission();

        submission1.persistAndFlush();
        submission2.persistAndFlush();

        // Act
        List<ContentSubmission> result = contentGraph.submissions(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @Transactional
    public void canFindRecentSubmissions() {
        // Arrange - Create test submissions
        ContentSubmission submission1 = new ContentSubmission.Builder()
                .withUrl("https://example.com/recent1")
                .withStatus(SubmissionStatus.SUBMITTED)
                .build();
        ContentSubmission submission2 = new ContentSubmission.Builder()
                .withUrl("https://example.com/recent2")
                .withTitle("Recent Article 2")
                .withSummary("Summary for recent article 2")
                .withStatus(SubmissionStatus.SUMMARIZED)
                .build();

        submission1.persistAndFlush();
        submission2.persistAndFlush();

        // Act
        List<ContentSubmission> result = contentGraph.recentSubmissions();

        // Assert
        assertNotNull(result);
    }

    @Test
    @Transactional
    public void canFindProcessableSubmissions() {
        // Arrange - Create test submissions
        ContentSubmission submission1 = new ContentSubmission.Builder()
                .withUrl("https://example.com/processable1")
                .withTitle("Processable Article 1")
                .withSummary("Summary for processable article 1")
                .withStatus(SubmissionStatus.SUMMARIZED)
                .build();
        ContentSubmission submission2 = new ContentSubmission.Builder()
                .withUrl("https://example.com/processable2")
                .withTitle("Processable Article 2")
                .withSummary("Summary for processable article 2")
                .withStatus(SubmissionStatus.SUMMARIZED)
                .build();

        submission1.persistAndFlush();
        submission2.persistAndFlush();

        // Act
        List<ContentSubmission> result = contentGraph.processableSubmissions();

        // Assert
        assertNotNull(result);
    }

    @Test
    @Transactional
    public void canSummarizeContent() {
        // Arrange
        ContentSubmission submission = new ContentSubmission.Builder()
                .withUrl("https://example.com/test")
                .withStatus(SubmissionStatus.SUBMITTED)
                .build();
        submission.persistAndFlush();

        SummarizeContent input = new SummarizeContent(
                submission.id,
                "Updated Test Title",
                "This is an updated test summary with more detailed information.");

        // Act
        ContentSubmission result = contentGraph.summarizeContent(input);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Test Title", result.title);
        assertEquals("This is an updated test summary with more detailed information.", result.summary);
        assertEquals(SubmissionStatus.SUMMARIZED, result.status);
        assertNotNull(result.dateModified);
    }

    @Test
    @Transactional
    public void canMarkContentForProcessing() {
        // Arrange
        ContentSubmission submission = TestObjectFactory.createSummarizedContentSubmission();
        submission.persistAndFlush();

        // Act
        ContentSubmission result = contentGraph.markForProcessing(new MarkForProcessing(submission.id));

        // Assert
        assertNotNull(result);
        assertEquals(SubmissionStatus.PROCESSING, result.status);
        assertNotNull(result.dateModified);
    }

    @Test
    @Transactional
    public void canMarkContentAsProcessed() {
        // Arrange
        ContentSubmission submission = TestObjectFactory.createSummarizedContentSubmission();
        submission.markForProcessing();
        submission.persistAndFlush();

        // Act
        ContentSubmission result = contentGraph.markAsProcessed(new MarkAsProcessed(submission.id));

        // Assert
        assertNotNull(result);
        assertEquals(SubmissionStatus.PROCESSED, result.status);
        assertNotNull(result.dateModified);
    }

    @Test
    @Transactional
    public void canCreatePodcastEpisode() {
        // Arrange
        CreatePodcastEpisode input = new CreatePodcastEpisode(
                "episodes/test-episode.mp3",
                "Weekly Tech Roundup #42",
                "In this episode, we cover:\n• AI developments\n• Cloud computing trends\n• Software engineering best practices",
                "This week we discuss the latest developments in technology including artificial intelligence breakthroughs, cloud computing innovations, and software engineering best practices that every developer should know.");

        // Act
        PodcastEpisode result = contentGraph.createPodcastEpisode(input);

        // Assert
        assertNotNull(result);
        assertEquals("Weekly Tech Roundup #42", result.title);
        assertEquals("episodes/test-episode.mp3", result.audioFilePath);
        assertEquals(
                "In this episode, we cover:\n• AI developments\n• Cloud computing trends\n• Software engineering best practices",
                result.showNotes);
        assertEquals(
                "This week we discuss the latest developments in technology including artificial intelligence breakthroughs, cloud computing innovations, and software engineering best practices that every developer should know.",
                result.description);
        assertNotNull(result.dateCreated);
        assertNotNull(result.episodeNumber);
    }

    @Test
    @Transactional
    public void canFindEpisodes() {
        // Arrange - Create test episodes
        PodcastEpisode episode1 = new TestObjectFactory.PodcastEpisodeBuilder()
                .withTitle("Tech Talk #1")
                .withEpisodeNumber(1)
                .build();
        PodcastEpisode episode2 = new TestObjectFactory.PodcastEpisodeBuilder()
                .withTitle("Tech Talk #2")
                .withEpisodeNumber(2)
                .build();

        episode1.persistAndFlush();
        episode2.persistAndFlush();

        // Act
        List<PodcastEpisode> result = contentGraph.episodes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @Transactional
    public void canGetStatistics() {
        // Arrange - Create test data
        ContentSubmission submission = TestObjectFactory.createSummarizedContentSubmission();
        PodcastEpisode episode = TestObjectFactory.createTechTalkEpisode();

        submission.persistAndFlush();
        episode.persistAndFlush();

        // Act
        var result = contentGraph.statistics();

        // Assert
        assertNotNull(result);
        assertNotNull(result.totalEpisodes);
        assertNotNull(result.submissionsLastWeek);
    }

    @Test
    @Transactional
    public void demonstrateTestObjectFactoryUsage() {
        // Object Mother pattern - predefined objects with sensible defaults
        ContentSubmission submission1 = TestObjectFactory.createSummarizedContentSubmission();
        ContentSubmission submission2 = TestObjectFactory.createProcessableSubmission();

        // Test Data Builder pattern - customized objects
        ContentSubmission customSubmission = new ContentSubmission.Builder()
                .withUrl("https://custom.example.com/article")
                .withTitle("Custom Article Title")
                .withSummary("Custom summary content")
                .withStatus(SubmissionStatus.PROCESSING)
                .build();

        PodcastEpisode episode = new TestObjectFactory.PodcastEpisodeBuilder()
                .withTitle("Custom Episode")
                .withEpisodeNumber(42)
                .withShowNotes("Custom show notes")
                .build();

        // Persist test data
        submission1.persistAndFlush();
        submission2.persistAndFlush();
        customSubmission.persistAndFlush();
        episode.persistAndFlush();

        // Verify objects were created with expected values
        assertEquals("https://custom.example.com/article", customSubmission.url);
        assertEquals("Custom Article Title", customSubmission.title);
        assertEquals("Custom summary content", customSubmission.summary);
        assertEquals(SubmissionStatus.PROCESSING, customSubmission.status);

        assertEquals("Custom Episode", episode.title);
        assertEquals(42, episode.episodeNumber);
        assertEquals("Custom show notes", episode.showNotes);
        assertNotNull(episode.audioFilePath);
        assertNotNull(episode.description);
    }
}
