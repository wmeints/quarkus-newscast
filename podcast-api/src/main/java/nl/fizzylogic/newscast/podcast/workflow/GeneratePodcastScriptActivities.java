package nl.fizzylogic.newscast.podcast.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;

@ActivityInterface
public interface GeneratePodcastScriptActivities {
    @ActivityMethod
    PodcastScript generatePodcastScript(GeneratePodcastScriptInput input);
}
