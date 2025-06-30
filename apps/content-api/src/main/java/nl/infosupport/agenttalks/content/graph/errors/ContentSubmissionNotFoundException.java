package nl.infosupport.agenttalks.content.graph.errors;

import io.smallrye.graphql.api.ErrorCode;

@ErrorCode("submission-not-found")
public class ContentSubmissionNotFoundException extends RuntimeException {
    public ContentSubmissionNotFoundException(long id) {
        super("Content submission with ID " + id + " not found");
    }
}
