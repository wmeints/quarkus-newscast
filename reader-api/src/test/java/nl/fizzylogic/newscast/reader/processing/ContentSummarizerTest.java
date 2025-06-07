package nl.fizzylogic.newscast.reader.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import nl.fizzylogic.newscast.reader.model.ContentSummarizationData;
import nl.fizzylogic.newscast.reader.model.ContentSummary;
import nl.fizzylogic.newscast.reader.service.ContentSummarizer;

@QuarkusTest
public class ContentSummarizerTest {

    @Inject
    ContentSummarizationStep contentSummarizer;

    @InjectMock
    ContentSummarizer summarizerAgent;

    @Test
    public void canSummarizeContent() {
        when(summarizerAgent.summarizeContent(anyString())).thenReturn(
                new ContentSummary("test", "test"));

        var processData = new ContentSummarizationData(
                1L, "http://localhost:3000/", "test", "text/plain");

        var result = contentSummarizer.process(JsonObject.mapFrom(processData));

        assertNotNull(result);
        assertEquals("test", result.summary);
        assertEquals("test", result.title);
    }
}
