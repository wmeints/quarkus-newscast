package nl.fizzylogic.newscast.podcast.model;

public class PodcastFragment {
    public String host;
    public String content;

    public PodcastFragment() {
        // Default constructor for serialization
    }

    public PodcastFragment(String host, String content) {
        this.host = host;
        this.content = content;
    }
}
