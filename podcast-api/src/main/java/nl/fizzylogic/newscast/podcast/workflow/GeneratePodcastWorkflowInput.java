package nl.fizzylogic.newscast.podcast.workflow;

import java.time.LocalDate;
import java.util.List;

import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;

public class GeneratePodcastWorkflowInput {
    public LocalDate startDate;
    public LocalDate endDate;
    public List<ContentSubmission> contentSubmissions;

    public GeneratePodcastWorkflowInput() {

    }

    public GeneratePodcastWorkflowInput(LocalDate startDate, LocalDate endDate,
            List<ContentSubmission> contentSubmissions) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.contentSubmissions = contentSubmissions;
    }
}
