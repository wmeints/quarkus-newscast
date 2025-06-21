package nl.fizzylogic.newscast.podcast.clients.buzzsprout.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateEpisodeResponse {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("title")
    public String title;

    @JsonProperty("description")
    public String description;

    @JsonProperty("summary")
    public String summary;

    @JsonProperty("audio_url")
    public String audioUrl;

    @JsonProperty("artwork_url")
    public String artworkUrl;

    @JsonProperty("public_url")
    public String publicUrl;

    @JsonProperty("episode_number")
    public Integer episodeNumber;

    @JsonProperty("season_number")
    public Integer seasonNumber;

    @JsonProperty("published")
    public Boolean published;

    public CreateEpisodeResponse() {
        // Default constructor for serialization
    }
}