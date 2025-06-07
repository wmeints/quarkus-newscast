package nl.fizzylogic.newscast.podcast.shared;

import java.util.List;

import net.datafaker.Faker;
import nl.fizzylogic.newscast.podcast.clients.content.model.ContentSubmission;
import nl.fizzylogic.newscast.podcast.clients.content.model.SubmissionStatus;
import nl.fizzylogic.newscast.podcast.model.PodcastFragment;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;
import nl.fizzylogic.newscast.podcast.model.PodcastSection;

public class TestObjectFactory {
    private static final Faker faker = new Faker();

    public static PodcastScript createPodcastScript() {
        var script = new PodcastScript();

        var segment = new PodcastSection();

        segment.title = "Test Segment";
        segment.fragments = List.of(
                new PodcastFragment(
                        "Joop Snijder",
                        "Hallo en welkom bij een nieuwe aflevering van Agent talks!"),
                new PodcastFragment(
                        "Willem Meints",
                        "Vandaag gaan we het hebben over de nieuwste ontwikkelingen in AI."));

        script.sections = List.of(segment);

        return script;
    }

    public static ContentSubmission createSummarizedSubmission() {
        var submission = new ContentSubmission();

        submission.title = faker.lorem().sentence();
        submission.summary = faker.lorem().paragraph();
        submission.url = faker.internet().url();
        submission.status = SubmissionStatus.SUMMARIZED;

        return submission;
    }
}
