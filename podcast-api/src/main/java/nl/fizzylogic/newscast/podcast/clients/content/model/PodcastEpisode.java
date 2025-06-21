package nl.fizzylogic.newscast.podcast.clients.content.model;

import java.time.LocalDateTime;

public class PodcastEpisode {
    public String title;
    public String audioFilePath;
    public LocalDateTime dateCreated;
    public int episodeNumber;
    public String showNotes;
    public String description;

    public PodcastEpisode() {

    }

    public PodcastEpisode(String title, String audioFilePath, String showNotes, String description) {
        this.title = title;
        this.audioFilePath = audioFilePath;
        this.dateCreated = LocalDateTime.now();
        this.showNotes = showNotes;
        this.description = description;
    }
}
