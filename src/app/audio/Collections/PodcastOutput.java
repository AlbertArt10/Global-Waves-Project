package app.audio.Collections;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class PodcastOutput {

    private final String name;
    private final ArrayList<String> episodes;

    public PodcastOutput(final Podcast podcast) {
        this.name = podcast.getName();
        this.episodes = new ArrayList<>();
        for (int i = 0; i < podcast.getEpisodes().size(); i++) {
            episodes.add(podcast.getEpisodes().get(i).getName());
        }
    }
}
