package nl.fizzylogic.newscast.podcast.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;

public class PodcastEpisodeData {
    public LocalDate startDate;
    public LocalDate endDate;
    public List<ContentSubmission> contentSubmissions;
    public String script;
    public PodcastScript parsedScript; // Parsed script for audio generation
    public Map<String, String> audioFiles; // Fragment ID to audio file path mapping

    public PodcastEpisodeData() {
        // Default constructor for serialization/deserialization
        this.audioFiles = new HashMap<>();
    }

    public PodcastEpisodeData(LocalDate startDate, LocalDate endDate,
            List<ContentSubmission> contentSubmissions) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.contentSubmissions = contentSubmissions;
    }

    public PodcastEpisodeData withPodcastScript(String script) {
        this.script = script;
        return this;
    }

    public void addAudioFile(String fragmentId, String audioFilePath) {
        this.audioFiles.put(fragmentId, audioFilePath);
    }

    public static PodcastEpisodeDataBuilder builder() {
        return new PodcastEpisodeDataBuilder();
    }

    public static class PodcastEpisodeDataBuilder {
        private final PodcastEpisodeData instance;

        public PodcastEpisodeDataBuilder() {
            this.instance = new PodcastEpisodeData();
        }

        public PodcastEpisodeDataBuilder withStartDate(LocalDate startDate) {
            instance.startDate = startDate;
            return this;
        }

        public PodcastEpisodeDataBuilder withEndDate(LocalDate endDate) {
            instance.endDate = endDate;
            return this;
        }

        public PodcastEpisodeDataBuilder withContentSubmissions(List<ContentSubmission> contentSubmissions) {
            instance.contentSubmissions = contentSubmissions;
            return this;
        }

        public PodcastEpisodeData build() {
            return instance;
        }
    }
}
