package nl.fizzylogic.newscast.reader.processing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.typesafe.api.ErrorOr;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import nl.fizzylogic.newscast.reader.clients.content.ContentClient;
import nl.fizzylogic.newscast.reader.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.reader.model.ContentSummarizationData;

@QuarkusTest
public class ContentSubmissionUpdaterTest {
    @InjectMock
    ContentClient contentClient;

    @Inject
    UpdateContentSubmissionStep contentSubmissionUpdater;

    @Test
    public void canUpdateContentSubmission() {
        when(contentClient.summarizeContent(any())).thenReturn(ErrorOr.of(new ContentSubmission()));

        var processData = new ContentSummarizationData(
                1L, "http://localhost:3000/", "test", "text/plain");

        processData = processData.withSummary("Test Title", "This is a test summary");

        contentSubmissionUpdater.updateContentSubmission(JsonObject.mapFrom(processData));

        verify(contentClient).summarizeContent(any());
    }
}
