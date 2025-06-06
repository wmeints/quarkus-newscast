package nl.fizzylogic.newscast.podcast.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.json.jackson.DatabindCodec;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.ElevenLabsClient;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.model.CreateSpeechRequest;
import nl.fizzylogic.newscast.podcast.exceptions.AudioGenerationException;
import nl.fizzylogic.newscast.podcast.model.PodcastEpisodeData;
import nl.fizzylogic.newscast.podcast.model.PodcastFragment;
import nl.fizzylogic.newscast.podcast.model.PodcastHost;
import nl.fizzylogic.newscast.podcast.model.PodcastScript;
import nl.fizzylogic.newscast.podcast.model.PodcastSection;

/**
 * Converts the podcast script into audio using ElevenLabs.
 */
@ApplicationScoped
public class GeneratePodcastAudioStep {
    private static final Logger logger = Logger.getLogger(GeneratePodcastAudioStep.class);
    private static final String AUDIO_OUTPUT_DIR = "/tmp/podcast-audio";
    private static final String INTRODUCTION_SECTION = "introduction";
    private static final String CONCLUSION_SECTION = "conclusion";
    
    @Inject
    @RestClient
    ElevenLabsClient elevenLabsClient;

    @ActivateRequestContext
    @Incoming("audio-generation-input")
    @Outgoing("audio-generation-output")
    public PodcastEpisodeData process(PodcastEpisodeData episodeData) {
        try {
            logger.info("Starting audio generation for podcast episode");
            
            // Parse the script if not already parsed
            if (episodeData.parsedScript == null && episodeData.script != null) {
                episodeData.parsedScript = parseScript(episodeData.script);
            }
            
            if (episodeData.parsedScript == null) {
                logger.warn("No script found to generate audio from");
                return episodeData;
            }
            
            // Ensure output directory exists
            createOutputDirectory();
            
            // Generate audio for each fragment (except introduction and conclusion)
            generateAudioForFragments(episodeData);
            
            logger.info("Completed audio generation for podcast episode");
            return episodeData;
            
        } catch (Exception ex) {
            logger.error("Failed to generate podcast audio", ex);
            throw new AudioGenerationException("Audio generation failed", ex);
        }
    }

    private PodcastScript parseScript(String scriptJson) {
        try {
            return DatabindCodec.mapper().readValue(scriptJson, PodcastScript.class);
        } catch (JsonProcessingException ex) {
            logger.error("Failed to parse podcast script JSON", ex);
            throw new AudioGenerationException("Failed to parse script JSON", ex);
        }
    }

    private void createOutputDirectory() {
        try {
            Path outputPath = Paths.get(AUDIO_OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
        } catch (IOException ex) {
            throw new AudioGenerationException("Failed to create audio output directory", ex);
        }
    }

    private void generateAudioForFragments(PodcastEpisodeData episodeData) {
        if (episodeData.parsedScript.sections == null) {
            logger.warn("No sections found in parsed script");
            return;
        }

        for (PodcastSection section : episodeData.parsedScript.sections) {
            if (section.fragments == null) {
                continue;
            }

            for (PodcastFragment fragment : section.fragments) {
                // Skip fragments that already have audio generated
                String fragmentId = generateFragmentId(fragment);
                if (episodeData.audioFiles.containsKey(fragmentId)) {
                    logger.debug("Audio already exists for fragment: " + fragmentId);
                    continue;
                }

                // Skip introduction and conclusion sections (use fixed audio)
                if (isSkippableSection(section, fragment)) {
                    logger.debug("Skipping introduction/conclusion fragment: " + fragmentId);
                    continue;
                }

                try {
                    String audioFilePath = generateAudioForFragment(fragment);
                    episodeData.addAudioFile(fragmentId, audioFilePath);
                    logger.info("Generated audio for fragment: " + fragmentId);
                } catch (Exception ex) {
                    logger.error("Failed to generate audio for fragment: " + fragmentId, ex);
                    throw new AudioGenerationException("Failed to generate audio for fragment: " + fragmentId, ex);
                }
            }
        }
    }

    private boolean isSkippableSection(PodcastSection section, PodcastFragment fragment) {
        // For now, we'll check if the fragment content suggests it's intro/conclusion
        // In a real implementation, sections might have type/name fields
        String content = fragment.content != null ? fragment.content.toLowerCase() : "";
        
        // Check for introduction patterns
        boolean isIntroduction = content.contains("welcome") && (content.contains("podcast") || content.contains("episode")) ||
                                content.contains("i'm ") && content.contains("hello") ||
                                content.contains("good morning") || content.contains("good afternoon");
        
        // Check for conclusion patterns  
        boolean isConclusion = content.contains("conclusion") || content.contains("wrap up") ||
                              content.contains("thank you for listening") || content.contains("that's all") ||
                              content.contains("goodbye") || content.contains("see you next") ||
                              content.contains("until next time");
        
        if (isIntroduction || isConclusion) {
            logger.debug("Skipping fragment (intro/conclusion detected): " + content.substring(0, Math.min(50, content.length())));
            return true;
        }
        
        return false;
    }

    private String generateAudioForFragment(PodcastFragment fragment) {
        // Get the voice ID for the host
        PodcastHost host = PodcastHost.findByName(fragment.host);
        if (host == null) {
            throw new AudioGenerationException("Host not found: " + fragment.host);
        }
        
        if (host.voiceId == null || host.voiceId.trim().isEmpty()) {
            throw new AudioGenerationException("No voice ID configured for host: " + fragment.host);
        }

        // Create the speech request
        CreateSpeechRequest request = new CreateSpeechRequest();
        request.text = fragment.content;
        request.modelId = "eleven_multilingual_v2"; // Default model
        
        logger.debug("Generating audio for host: " + fragment.host + " with voice ID: " + host.voiceId);
        
        // Call ElevenLabs API
        Response response = elevenLabsClient.createSpeech(host.voiceId, "mp3_44100_128", request);
        
        if (response.getStatus() != 200) {
            throw new AudioGenerationException("ElevenLabs API returned status: " + response.getStatus());
        }
        
        // Save the audio file
        String fileName = "fragment_" + UUID.randomUUID().toString() + ".mp3";
        Path audioFilePath = Paths.get(AUDIO_OUTPUT_DIR, fileName);
        
        try (var audioStream = (java.io.InputStream) response.getEntity()) {
            Files.copy(audioStream, audioFilePath, StandardCopyOption.REPLACE_EXISTING);
            return audioFilePath.toString();
        } catch (IOException ex) {
            throw new AudioGenerationException("Failed to save audio file: " + fileName, ex);
        }
    }

    private String generateFragmentId(PodcastFragment fragment) {
        // Generate a unique ID based on fragment content and host
        return String.valueOf((fragment.host + fragment.content).hashCode());
    }
}
