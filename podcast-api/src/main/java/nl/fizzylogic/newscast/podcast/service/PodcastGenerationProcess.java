package nl.fizzylogic.newscast.podcast.service;

import java.time.LocalDate;
import java.util.UUID;

import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.inject.Inject;
import nl.fizzylogic.newscast.podcast.clients.content.ContentClient;
import nl.fizzylogic.newscast.podcast.workflow.GeneratePodcastWorkflow;
import nl.fizzylogic.newscast.podcast.workflow.GeneratePodcastWorkflowInput;

public class PodcastGenerationProcess {
    @Inject
    ContentClient contentClient;

    @Inject
    WorkflowClient workflowClient;

    Logger logger = Logger.getLogger(PodcastGenerationProcess.class);

    @Scheduled(cron = "0 0 18 ? * FRI") // Every Friday at 18:00
    public void start() {
        var currentDate = LocalDate.now();
        var startDate = currentDate.minusDays(7); // One week ago

        var processableSubmissions = contentClient.findProcessableSubmissions(startDate, currentDate);

        logger.infof(
                "Found %d processable submissions for the week starting %s",
                processableSubmissions.size(), startDate);

        if (!processableSubmissions.isEmpty()) {
            logger.infof(
                    "Triggering podcast generation for submissions from %s to %s",
                    startDate, currentDate);

            var workflowInput = new GeneratePodcastWorkflowInput(startDate, currentDate, processableSubmissions);

            var workflowOptions = WorkflowOptions.newBuilder()
                    .setTaskQueue("<default>")
                    .setWorkflowId(UUID.randomUUID().toString())
                    .build();

            var workflow = workflowClient.newWorkflowStub(GeneratePodcastWorkflow.class, workflowOptions);

            WorkflowClient.start(workflow::generatePodcast, workflowInput);
        }
    }
}
