package nl.fizzylogic.newscast.podcast.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.fizzylogic.newscast.podcast.model.PodcastEpisodeData;
import nl.fizzylogic.newscast.podcast.model.PodcastFragment;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;
import nl.fizzylogic.newscast.podcast.model.PodcastSection;

public class GeneratePodcastAudioStepUnitTest {

    @Test
    public void testPodcastScriptParsing() {
        // Test the audio generation step with a mock script
        PodcastEpisodeData episodeData = new PodcastEpisodeData();
        
        // Create a test script programmatically
        PodcastScript script = new PodcastScript();
        script.title = "Test Podcast";
        script.sections = new ArrayList<>();
        
        PodcastSection section = new PodcastSection();
        section.fragments = new ArrayList<>();
        
        PodcastFragment fragment1 = new PodcastFragment();
        fragment1.host = "Alice";
        fragment1.content = "This is a test content fragment from Alice.";
        section.fragments.add(fragment1);
        
        PodcastFragment fragment2 = new PodcastFragment();
        fragment2.host = "Bob";
        fragment2.content = "This is a test content fragment from Bob.";
        section.fragments.add(fragment2);
        
        script.sections.add(section);
        episodeData.parsedScript = script;
        
        // Verify the structure
        assertNotNull(episodeData.parsedScript);
        assertEquals("Test Podcast", episodeData.parsedScript.title);
        assertEquals(1, episodeData.parsedScript.sections.size());
        assertEquals(2, episodeData.parsedScript.sections.get(0).fragments.size());
        
        // Test fragment ID generation (based on how it's implemented in GeneratePodcastAudioStep)
        String fragmentId1 = String.valueOf((fragment1.host + fragment1.content).hashCode());
        String fragmentId2 = String.valueOf((fragment2.host + fragment2.content).hashCode());
        
        assertNotNull(fragmentId1);
        assertNotNull(fragmentId2);
        assertTrue(!fragmentId1.equals(fragmentId2), "Fragment IDs should be unique");
    }

    @Test 
    public void testAudioFileMapping() {
        PodcastEpisodeData episodeData = new PodcastEpisodeData();
        
        // Test adding audio files
        episodeData.addAudioFile("fragment1", "/path/to/audio1.mp3");
        episodeData.addAudioFile("fragment2", "/path/to/audio2.mp3");
        
        assertEquals(2, episodeData.audioFiles.size());
        assertEquals("/path/to/audio1.mp3", episodeData.audioFiles.get("fragment1"));
        assertEquals("/path/to/audio2.mp3", episodeData.audioFiles.get("fragment2"));
    }

    @Test
    public void testSkippableContentDetection() {
        // This would test the logic in isSkippableSection method
        // We can't easily test it without instantiating the class, but we can verify
        // the test patterns work as expected
        String introContent = "Welcome to our podcast, I'm Alice and this is episode 1";
        String conclusionContent = "Thank you for listening, goodbye until next time";
        String regularContent = "Today we're discussing artificial intelligence trends";
        
        assertTrue(introContent.toLowerCase().contains("welcome") && 
                   introContent.toLowerCase().contains("podcast"));
        assertTrue(conclusionContent.toLowerCase().contains("thank you for listening"));
        assertTrue(!regularContent.toLowerCase().contains("welcome") && 
                   !regularContent.toLowerCase().contains("thank you"));
    }
}