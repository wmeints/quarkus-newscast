package nl.fizzylogic.newscast.podcast.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.json.jackson.DatabindCodec;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;

public class PodcastScriptParsingTest {

    @Test
    public void canParseValidPodcastScript() throws JsonProcessingException {
        String testScript = """
        {
            "title": "Test Podcast",
            "sections": [
                {
                    "fragments": [
                        {
                            "host": "Alice",
                            "content": "This is a test content fragment from Alice."
                        },
                        {
                            "host": "Bob", 
                            "content": "This is a test content fragment from Bob."
                        }
                    ]
                }
            ]
        }
        """;

        PodcastScript script = DatabindCodec.mapper().readValue(testScript, PodcastScript.class);
        
        assertNotNull(script);
        assertEquals("Test Podcast", script.title);
        assertNotNull(script.sections);
        assertEquals(1, script.sections.size());
        assertEquals(2, script.sections.get(0).fragments.size());
        assertEquals("Alice", script.sections.get(0).fragments.get(0).host);
        assertEquals("Bob", script.sections.get(0).fragments.get(1).host);
    }
}