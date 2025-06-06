package nl.fizzylogic.newscast.podcast.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class PodcastHost extends PanacheEntity {
    public String name;
    public String styleInstructions;
    public String languagePatterns;
    public String voiceId; // ElevenLabs voice ID for audio generation
    public int index; // The index to sort hosts by

    public static PodcastHostBuilder builder() {
        return new PodcastHostBuilder();
    }

    public static PodcastHost findByIndex(int index) {
        return find("index", index).firstResult();
    }

    public static PodcastHost findByName(String name) {
        return find("name", name).firstResult();
    }

    public static class PodcastHostBuilder {
        private final PodcastHost host;

        public PodcastHostBuilder() {
            this.host = new PodcastHost();
        }

        public PodcastHostBuilder withName(String name) {
            host.name = name;
            return this;
        }

        public PodcastHostBuilder withStyleInstructions(String styleInstructions) {
            host.styleInstructions = styleInstructions;
            return this;
        }

        public PodcastHostBuilder withLanguagePatterns(String languagePatterns) {
            host.languagePatterns = languagePatterns;
            return this;
        }

        public PodcastHostBuilder withVoiceId(String voiceId) {
            host.voiceId = voiceId;
            return this;
        }

        public PodcastHostBuilder withIndex(int index) {
            host.index = index;
            return this;
        }

        public PodcastHost build() {
            return host;
        }

    }
}
