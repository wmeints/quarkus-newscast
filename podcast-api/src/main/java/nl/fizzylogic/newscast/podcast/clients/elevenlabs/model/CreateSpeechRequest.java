package nl.fizzylogic.newscast.podcast.clients.elevenlabs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateSpeechRequest {
    @JsonProperty("next_text")
    public String nextText;

    @JsonProperty("previous_text")
    public String previousText;

    @JsonProperty("text")
    public String text;

    @JsonProperty("model_id")
    public String modelId;

    public CreateSpeechRequest() {
        // Default constructor for serialization
    }

    public CreateSpeechRequest(String text) {
        this.text = text;
    }
}
