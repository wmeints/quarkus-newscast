package nl.fizzylogic.newscast.podcast.exceptions;

/**
 * Exception thrown when audio generation fails.
 * This exception should trigger message requeuing for retry.
 */
public class AudioGenerationException extends RuntimeException {
    public AudioGenerationException(String message) {
        super(message);
    }

    public AudioGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}