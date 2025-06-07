package nl.fizzylogic.newscast.podcast.workflow;

import java.util.List;

import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;

public class GeneratePodcastScriptInput {
    public List<ContentSubmission> contentSubmissions;

    public GeneratePodcastScriptInput() {

    }

    public GeneratePodcastScriptInput(List<ContentSubmission> contentSubmissions) {
        this.contentSubmissions = contentSubmissions;
    }
}
