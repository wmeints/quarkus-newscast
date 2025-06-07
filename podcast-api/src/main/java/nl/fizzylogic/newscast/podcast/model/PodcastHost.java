package nl.fizzylogic.newscast.podcast.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "podcast_host")
public class PodcastHost extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "name", columnDefinition = "varchar(250)")
    public String name;

    @Column(name = "style_instructions", columnDefinition = "text")
    public String styleInstructions;

    @Column(name = "language_patterns", columnDefinition = "text")
    public String languagePatterns;

    @Column(name = "voice_id", columnDefinition = "varchar(250)")
    public String voiceId;

    public PodcastHost() {
        // Default constructor for JPA
    }

    public PodcastHost(String name, String styleInstructions, String languagePatterns, String voiceId, int index) {
        this.name = name;
        this.styleInstructions = styleInstructions;
        this.languagePatterns = languagePatterns;
        this.voiceId = voiceId;
        this.index = index;
    }

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

        public PodcastHostBuilder withIndex(int index) {
            host.index = index;
            return this;
        }

        public PodcastHostBuilder withVoiceId(String voiceId) {
            host.voiceId = voiceId;
            return this;
        }

        public PodcastHost build() {
            return host;
        }
    }
}
