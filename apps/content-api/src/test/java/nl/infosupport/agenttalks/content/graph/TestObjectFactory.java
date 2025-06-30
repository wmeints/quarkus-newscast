package nl.infosupport.agenttalks.content.graph;

import nl.infosupport.agenttalks.content.model.ContentSubmission;
import nl.infosupport.agenttalks.content.model.PodcastEpisode;
import nl.infosupport.agenttalks.content.model.SubmissionStatus;

/**
 * Test Object Factory following the Object Mother and Test Data Builder
 * patterns.
 * Provides convenient methods to create test objects with sensible defaults.
 */
public class TestObjectFactory {

    // Object Mother methods for ContentSubmission
    public static ContentSubmission createContentSubmission() {
        return new ContentSubmission.Builder().build();
    }

    public static ContentSubmission createSubmittedContentSubmission() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/submitted-article")
                .withStatus(SubmissionStatus.SUBMITTED)
                .build();
    }

    public static ContentSubmission createSummarizedContentSubmission() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/summarized-article")
                .withTitle("Test Article Title")
                .withSummary("This is a comprehensive test summary that covers the main points of the article.")
                .withStatus(SubmissionStatus.SUMMARIZED)
                .build();
    }

    public static ContentSubmission createProcessingContentSubmission() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/processing-article")
                .withTitle("Processing Article Title")
                .withSummary("This article is currently being processed for podcast generation.")
                .withStatus(SubmissionStatus.PROCESSING)
                .build();
    }

    public static ContentSubmission createProcessedContentSubmission() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/processed-article")
                .withTitle("Processed Article Title")
                .withSummary("This article has been successfully processed and included in a podcast episode.")
                .withStatus(SubmissionStatus.PROCESSED)
                .build();
    }

    // Object Mother methods for PodcastEpisode
    public static PodcastEpisode createPodcastEpisode() {
        return new PodcastEpisodeBuilder().build();
    }

    public static PodcastEpisode createTechTalkEpisode() {
        return new PodcastEpisodeBuilder()
                .withTitle("Tech Talk Weekly")
                .withEpisodeNumber(1)
                .withShowNotes("In this episode, we discuss the latest trends in software development.")
                .withDescription("A comprehensive overview of current technology trends and innovations.")
                .build();
    }

    public static PodcastEpisode createAINewsEpisode() {
        return new PodcastEpisodeBuilder()
                .withTitle("AI News Roundup")
                .withEpisodeNumber(2)
                .withShowNotes("Latest developments in artificial intelligence and machine learning.")
                .withDescription("Covering breakthrough AI research and practical applications in industry.")
                .build();
    }

    // Additional convenience methods for specific test scenarios
    public static ContentSubmission createRecentSubmission() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/recent-article")
                .withStatus(SubmissionStatus.SUBMITTED)
                .build();
    }

    public static ContentSubmission createProcessableSubmission() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/processable-article")
                .withTitle("Processable Article")
                .withSummary("This article is ready to be processed into a podcast episode.")
                .withStatus(SubmissionStatus.SUMMARIZED)
                .build();
    }

    public static ContentSubmission createTestSubmissionForSummarization() {
        return new ContentSubmission.Builder()
                .withUrl("https://example.com/test-article")
                .withStatus(SubmissionStatus.SUBMITTED)
                .build();
    }

    // Test Data Builder for PodcastEpisode
    public static class PodcastEpisodeBuilder {
        private String title = "Default Podcast Episode";
        private String audioFilePath = "episodes/default-episode.mp3";
        private int episodeNumber = 1;
        private String showNotes = "Default show notes for this episode.";
        private String description = "Default description for this podcast episode.";

        public PodcastEpisodeBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public PodcastEpisodeBuilder withAudioFilePath(String audioFilePath) {
            this.audioFilePath = audioFilePath;
            return this;
        }

        public PodcastEpisodeBuilder withEpisodeNumber(int episodeNumber) {
            this.episodeNumber = episodeNumber;
            this.audioFilePath = "episodes/episode-" + episodeNumber + ".mp3";
            return this;
        }

        public PodcastEpisodeBuilder withShowNotes(String showNotes) {
            this.showNotes = showNotes;
            return this;
        }

        public PodcastEpisodeBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public PodcastEpisode build() {
            return new PodcastEpisode(title, audioFilePath, episodeNumber, showNotes, description);
        }
    }
}
