package nl.infosupport.agenttalks.content.graph;

import java.util.List;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import nl.infosupport.agenttalks.content.eventbus.ContentSubmissionCreated;
import nl.infosupport.agenttalks.content.eventbus.EventPublisher;
import nl.infosupport.agenttalks.content.graph.errors.ContentSubmissionNotFoundException;
import nl.infosupport.agenttalks.content.graph.input.CreatePodcastEpisode;
import nl.infosupport.agenttalks.content.graph.input.MarkAsProcessed;
import nl.infosupport.agenttalks.content.graph.input.MarkForProcessing;
import nl.infosupport.agenttalks.content.graph.input.SubmitContent;
import nl.infosupport.agenttalks.content.graph.input.SummarizeContent;
import nl.infosupport.agenttalks.content.model.ApplicationStatistics;
import nl.infosupport.agenttalks.content.model.ContentSubmission;
import nl.infosupport.agenttalks.content.model.PodcastEpisode;

@GraphQLApi
@RequestScoped
public class ContentGraph {
    @Inject
    EventPublisher eventPublisher;

    @Query
    @Description("Finds all content submissions")
    public List<ContentSubmission> submissions(int pageIndex, int pageSize) {
        return ContentSubmission.findAll(Sort.by("dateCreated").descending()).page(pageIndex, pageSize).list();
    }

    @Query
    @Transactional
    @Description("Finds all podcast episodes")
    public List<PodcastEpisode> episodes() {
        return PodcastEpisode.findAll().list();
    }

    @Query
    @Transactional
    @Description("Finds the content submissions that were created during the week")
    public List<ContentSubmission> recentSubmissions() {
        return ContentSubmission.findRecentlySubmitted().list();
    }

    @Query
    @Transactional
    @Description("Finds processable content submissions for the current week")
    public List<ContentSubmission> processableSubmissions() {
        return ContentSubmission.findProcessable().list();
    }

    @Query
    @Transactional
    @Description("Get statistics about the application")
    public ApplicationStatistics statistics() {
        var podcasts = PodcastEpisode.count();
        var submissions = ContentSubmission.findRecentlySubmitted().count();

        return new ApplicationStatistics(podcasts, submissions);
    }

    @Mutation
    @Transactional
    @Description("Submits content for processing")
    public ContentSubmission submitContent(SubmitContent input) {
        ContentSubmission submission = new ContentSubmission(input.url);
        submission.persistAndFlush();

        eventPublisher.publishContentSubmissionCreated(
                new ContentSubmissionCreated(
                        submission.id,
                        submission.url,
                        submission.dateCreated));

        return submission;
    }

    @Mutation
    @Transactional
    @Description("Creates a new podcast episode")
    public PodcastEpisode createPodcastEpisode(CreatePodcastEpisode input) {
        var episode = new PodcastEpisode(
                input.title, input.audioFile,
                PodcastEpisode.getNextEpisodeNumber(),
                input.showNotes, input.description);

        episode.persistAndFlush();

        return episode;
    }

    @Mutation
    @Transactional
    @Description("Updates a content submission with a summary")
    public ContentSubmission summarizeContent(SummarizeContent input) {
        ContentSubmission submission = ContentSubmission.findById(input.id);

        if (submission == null) {
            throw new ContentSubmissionNotFoundException(input.id);
        }

        submission.summarize(input.title, input.summary);
        submission.persistAndFlush();

        return submission;
    }

    @Mutation
    @Transactional
    @Description("Marks a content submission for processing")
    public ContentSubmission markForProcessing(MarkForProcessing input) {
        ContentSubmission submission = ContentSubmission.findById(input.id);

        if (submission == null) {
            throw new ContentSubmissionNotFoundException(input.id);
        }

        submission.markForProcessing();
        submission.persistAndFlush();

        return submission;
    }

    @Mutation
    @Transactional
    @Description("Marks a content submission as processed")
    public ContentSubmission markAsProcessed(MarkAsProcessed input) {
        ContentSubmission submission = ContentSubmission.findById(input.id);

        if (submission == null) {
            throw new ContentSubmissionNotFoundException(input.id);
        }

        submission.markAsProcessed();
        submission.persistAndFlush();

        return submission;
    }
}
