package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import lombok.Getter;

import java.util.List;

/**
 * The type Album.
 */
@Getter
public final class Album extends AudioCollection {

    private final String description;
    private final List<Song> songs;
    private final Integer releaseYear;
    private int totalListens;

    /**
     * Instantiates a new Album.
     *
     * @param name        the name
     * @param description the description
     * @param owner       the owner
     * @param songs       the songs
     * @param releaseYear the release year
     */
    public Album(final String name, final String description, final String owner,
                 final List<Song> songs, final Integer releaseYear) {
        super(name, owner);
        this.songs = songs;
        this.description = description;
        this.releaseYear = releaseYear;
        totalListens = 0;
    }

    @Override
    public int getNumberOfTracks() {
        return songs.size();
    }

    @Override
    public AudioFile getTrackByIndex(final int index) {
        return songs.get(index);
    }

    @Override
    public boolean matchesDescription(final String desc) {
        return description.equals(desc);
    }

    @Override
    public boolean containsTrack(final AudioFile track) {
        return songs.contains(track);
    }
}
