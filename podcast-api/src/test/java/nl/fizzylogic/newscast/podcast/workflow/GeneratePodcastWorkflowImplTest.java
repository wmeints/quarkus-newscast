package nl.fizzylogic.newscast.podcast.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.worker.WorkerFactory;
import jakarta.inject.Inject;
import nl.fizzylogic.newscast.podcast.clients.content.model.SubmissionStatus;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;
import nl.fizzylogic.newscast.podcast.shared.TestObjectFactory;

@QuarkusTest
public class GeneratePodcastWorkflowImplTest {
    @Inject
    private WorkflowClient workflowClient;

    @Inject
    WorkerFactory workerFactory;

    @InjectMock
    ContentMetadataActivities contentMetadataActivities;

    @InjectMock
    GeneratePodcastScriptActivities generatePodcastScriptActivities;

    @InjectMock
    GeneratePodcastAudioActivities generatePodcastAudioActivities;

    @Test
    public void canRunWorkflowWithMockedActivities() {
        var script = TestObjectFactory.createPodcastScript();

        when(generatePodcastScriptActivities.generatePodcastScript(any())).thenReturn(script);
        when(generatePodcastAudioActivities.generateSpeech(any())).thenReturn("data/test.mp3");
        when(generatePodcastAudioActivities.concatenateAudioFragments(any())).thenReturn("data/final.mp3");

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue("<default>")
                .setWorkflowRunTimeout(Duration.ofSeconds(2))
                .build();

        var workflowInstance = workflowClient.newWorkflowStub(
                GeneratePodcastWorkflow.class,
                workflowOptions);

        var workflowInput = new GeneratePodcastWorkflowInput(LocalDate.now().minusDays(7), LocalDate.now(), List.of(
                TestObjectFactory.createSummarizedSubmission(),
                TestObjectFactory.createSummarizedSubmission()));

        workflowInstance.generatePodcast(workflowInput);

        var fragmentsArgumentCapture = ArgumentCaptor.forClass(List.class);
        var lockedContentSubmissions = ArgumentCaptor.forClass(List.class);
        var processedContentSubmissions = ArgumentCaptor.forClass(List.class);

        verify(generatePodcastScriptActivities).generatePodcastScript(any());
        verify(generatePodcastAudioActivities, times(2)).generateSpeech(any());
        verify(generatePodcastAudioActivities).concatenateAudioFragments(fragmentsArgumentCapture.capture());
        verify(contentMetadataActivities).lockContentSubmissions(lockedContentSubmissions.capture());
        verify(contentMetadataActivities).markContentSubmissionsAsProcessed(processedContentSubmissions.capture());

        assertEquals(2, fragmentsArgumentCapture.getValue().size(),
                "Expected two audio fragments to be generated for the two podcast hosts in the script.");

        assertEquals(2, lockedContentSubmissions.getValue().size(),
                "Expected two content submissions to be locked for the two podcast hosts in the script.");

        assertEquals(2, processedContentSubmissions.getValue().size(),
                "Expected two content submissions to be marked as processed for the two podcast hosts in the script.");
    }
}
