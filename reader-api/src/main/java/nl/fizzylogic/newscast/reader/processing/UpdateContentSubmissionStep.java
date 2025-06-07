package nl.fizzylogic.newscast.reader.processing;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.smallrye.graphql.client.GraphQLClientException;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import nl.fizzylogic.newscast.reader.clients.content.ContentClient;
import nl.fizzylogic.newscast.reader.clients.content.model.SummarizeContent;
import nl.fizzylogic.newscast.reader.model.ContentSummarizationData;

@ApplicationScoped
public class UpdateContentSubmissionStep {
    @Inject
    ContentClient contentClient;

    Logger logger = Logger.getLogger(UpdateContentSubmissionStep.class);

    @Incoming("content-submission-updater-input")
    public void updateContentSubmission(JsonObject message) {
        var data = message.mapTo(ContentSummarizationData.class);

        logger.infof(
                "Updating content submission with ID: %d",
                data.contentSubmissionId);

        try {
            SummarizeContent input = new SummarizeContent(
                    data.contentSubmissionId,
                    data.title,
                    data.summary);

            var response = contentClient.summarizeContent(input);

            if (response.hasErrors()) {
                throw new RuntimeException("Failed to update content submission");
            }
        } catch (GraphQLClientException ex) {
            logger.errorf("Failed to update content submission: %s", ex.getMessage());
            throw ex;
        }
    }
}
