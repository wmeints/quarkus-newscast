package nl.fizzylogic.newscast.podcast.clients.elevenlabs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.fizzylogic.newscast.podcast.clients.elevenlabs.model.CreateSpeechRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "elevenlabs.api")
public interface ElevenLabsClient {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/v1/text-to-speech/{voiceId}")
    Response createSpeech(@PathParam("voiceId") String voiceId, @QueryParam("output_format") String outputFormat, CreateSpeechRequest request);
}
