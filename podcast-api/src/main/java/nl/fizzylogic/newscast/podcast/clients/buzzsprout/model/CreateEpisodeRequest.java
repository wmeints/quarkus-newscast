package nl.fizzylogic.newscast.podcast.clients.buzzsprout.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateEpisodeRequest {
    @JsonProperty("title")
    public String title;

    @JsonProperty("description")
    public String description;

    @JsonProperty("summary")
    public String summary;

    @JsonProperty("audio_url")
    public String audioUrl;

    @JsonProperty("published")
    public Boolean published = true;

    @JsonProperty("explicit")
    public Boolean explicit = false;

    @JsonProperty("season_number")
    public Integer seasonNumber = 1;

    @JsonProperty("episode_number")
    public Integer episodeNumber = 1;

    public CreateEpisodeRequest() {
        // Default constructor for serialization
    }

    public CreateEpisodeRequest(int seasonNumber, int episodeNumber, String title, String description, String summary,
            String audioUrl) {
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.description = description;
        this.summary = summary;
        this.audioUrl = audioUrl;
    }
}