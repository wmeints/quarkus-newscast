package nl.fizzylogic.newscast.reader.processing;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import nl.fizzylogic.newscast.reader.model.ContentSummarizationData;
import nl.fizzylogic.newscast.reader.service.ContentSummarizer;

@ApplicationScoped
public class ContentSummarizationStep {
    @Inject
    ContentSummarizer summarizerAgent;

    @ActivateRequestContext
    @Incoming("content-summarizer-input")
    @Outgoing("content-summarizer-output")
    public ContentSummarizationData process(JsonObject message) {
        var contentSummarizationData = message.mapTo(ContentSummarizationData.class);
        var contentSummary = summarizerAgent.summarizeContent(contentSummarizationData.body);
        return contentSummarizationData.withSummary(contentSummary.title, contentSummary.summary);
    }
}
