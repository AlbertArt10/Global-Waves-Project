package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import lombok.Getter;

import java.util.List;

@Getter
public final class Podcast extends AudioCollection {

    private final List<Episode> episodes;

    public Podcast(final String name, final String owner, final List<Episode> episodes) {
        super(name, owner);
        this.episodes = episodes;
    }

    @Override
    public int getNumberOfTracks() {
        return episodes.size();
    }

    @Override
    public AudioFile getTrackByIndex(final int index) {
        return episodes.get(index);
    }

    @Override
    public boolean containsTrack(final AudioFile track) {
        return episodes.contains(track);
    }
}
