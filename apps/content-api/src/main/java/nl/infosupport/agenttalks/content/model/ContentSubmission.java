package nl.infosupport.agenttalks.content.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import org.eclipse.microprofile.graphql.Type;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Type
@Entity(name = "content_submission")
public class ContentSubmission extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "url", nullable = false, columnDefinition = "varchar(1000)")
    public String url;

    @Column(name = "title", nullable = true, columnDefinition = "varchar(500)")
    public String title;

    @Column(name = "summary", columnDefinition = "text", nullable = true)
    public String summary;

    @Column(name = "date_created", columnDefinition = "timestamp", nullable = false)
    public LocalDateTime dateCreated;

    @Column(name = "date_modified", columnDefinition = "timestamp", nullable = true)
    public LocalDateTime dateModified;

    @Column(name = "submission_status", columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    public SubmissionStatus status;

    protected ContentSubmission() {

    }

    public ContentSubmission(String url) {
        this.url = url;
        this.dateCreated = LocalDateTime.now();
        this.status = SubmissionStatus.SUBMITTED;
    }

    public void summarize(String title, String summary) {
        this.title = title;
        this.summary = summary;
        this.status = SubmissionStatus.SUMMARIZED;
        this.dateModified = LocalDateTime.now();
    }

    public void markForProcessing() {
        this.status = SubmissionStatus.PROCESSING;
        this.dateModified = LocalDateTime.now();
    }

    public void markAsProcessed() {
        this.status = SubmissionStatus.PROCESSED;
        this.dateModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ContentSubmission{" + "id=" + id + ", url=" + url + ", status=" + status.toString() + "}";

    }

    public static PanacheQuery<ContentSubmission> findProcessable() {
        var startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        var queryParams = Parameters
                .with("startDate", startOfWeek.atStartOfDay())
                .and("status", SubmissionStatus.SUMMARIZED);

        return find(
                "dateCreated >= :startDate and status = :status",
                Sort.by("dateCreated"), queryParams);
    }

    public static PanacheQuery<ContentSubmission> findRecentlySubmitted() {
        var startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        var queryParams = Parameters.with("startDate", startOfWeek.atStartOfDay());

        return find("dateCreated >= :startDate", Sort.by("dateCreated").descending(), queryParams);
    }

    /**
     * Builder class for creating ContentSubmission instances with flexible
     * configuration.
     * Follows the builder pattern for improved readability and maintainability.
     */
    public static class Builder {
        private String url = "https://example.com/default-article";
        private String title;
        private String summary;
        private SubmissionStatus status = SubmissionStatus.SUBMITTED;

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder withStatus(SubmissionStatus status) {
            this.status = status;
            return this;
        }

        public ContentSubmission build() {
            ContentSubmission submission = new ContentSubmission(url);
            submission.status = status;

            submission.title = title;
            submission.summary = summary;
            submission.status = status;
            submission.dateCreated = LocalDateTime.now();
            submission.dateModified = LocalDateTime.now();

            return submission;
        }
    }
}
