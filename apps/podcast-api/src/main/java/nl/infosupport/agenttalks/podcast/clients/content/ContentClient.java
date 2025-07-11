package nl.infosupport.agenttalks.podcast.clients.content;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.graphql.client.typesafe.api.ErrorOr;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import nl.infosupport.agenttalks.podcast.clients.content.model.ContentSubmission;
import nl.infosupport.agenttalks.podcast.clients.content.model.CreatePodcastEpisode;
import nl.infosupport.agenttalks.podcast.clients.content.model.MarkAsProcessed;
import nl.infosupport.agenttalks.podcast.clients.content.model.MarkForProcessing;
import nl.infosupport.agenttalks.podcast.clients.content.model.PodcastEpisode;

@GraphQLClientApi(configKey = "content-api")
public interface ContentClient {
    @Mutation
    ErrorOr<ContentSubmission> markForProcessing(MarkForProcessing input);

    @Mutation
    ErrorOr<ContentSubmission> markAsProcessed(MarkAsProcessed input);

    @Mutation
    ErrorOr<PodcastEpisode> createPodcastEpisode(CreatePodcastEpisode input);

    @Query
    List<ContentSubmission> findProcessableSubmissions(LocalDate startDate, LocalDate endDate);
}
