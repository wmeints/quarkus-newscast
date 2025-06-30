package nl.infosupport.agenttalks.content.model;

public class ApplicationStatistics {
    public long totalEpisodes;
    public long submissionsLastWeek;

    public ApplicationStatistics(long totalEpisodes, long submissionsLastWeek) {
        this.totalEpisodes = totalEpisodes;
        this.submissionsLastWeek = submissionsLastWeek;
    }
}
