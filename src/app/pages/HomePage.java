package app.pages;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Home page.
 */
public final class HomePage implements Page {
    @Getter
    private final String type = "home";
    private final List<Song> likedSongs;
    private final List<Playlist> followedPlaylists;
    @Getter @Setter
    private Song songRecommendation;
    @Getter @Setter
    private Playlist playlistRecommendation;
    private final int limit = 5;

    /**
     * Instantiates a new Home page.
     *
     * @param user the user
     */
    public HomePage(final User user) {
        likedSongs = user.getLikedSongs();
        followedPlaylists = user.getFollowedPlaylists();
    }

    @Override
    public String printCurrentPage() {
        StringBuilder pageContent = new StringBuilder();
        pageContent.append("Liked songs:\n\t")
                .append(formatSongList(likedSongs));

        pageContent.append("\n\nFollowed playlists:\n\t")
                .append(formatPlaylistList(followedPlaylists));

        if (songRecommendation != null) {
            pageContent.append("\n\nSong recommendations:\n\t[")
                    .append(songRecommendation.getName())
                    .append("]");
        } else {
            pageContent.append("\n\nSong recommendations:\n\t[]");
        }

        if (playlistRecommendation != null) {
            pageContent.append("\n\nPlaylists recommendations:\n\t[")
                    .append(playlistRecommendation.getName())
                    .append("]");
        } else {
            pageContent.append("\n\nPlaylists recommendations:\n\t[]");
        }

        return pageContent.toString();
    }

    /**
     * Formatează o listă de melodii într-un șir de caractere, sortând melodiile după numărul
     *            de aprecieri și limitând numărul acestora la un număr specificat.
     *
     * @param songs Lista de melodii care trebuie formatată.
     * @return Un șir de caractere care reprezintă lista de melodii.
     */
    private String formatSongList(final List<Song> songs) {
        String result = songs.stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .limit(limit)
                .map(Song::getName)
                .collect(Collectors.joining(", "));
        return result.isEmpty() ? "[]" : "[" + result + "]";
    }

    /**
     * Formatează o listă de playlist-uri într-un șir de caractere, sortând playlist-urile
     *            după suma aprecierilor melodiilor conținute și limitând numărul acestora
     *            la un număr specificat.
     *
     * @param playlists Lista de playlist-uri care trebuie formatată.
     * @return Un șir de caractere care reprezintă lista de playlist-uri.
     */
    private String formatPlaylistList(final List<Playlist> playlists) {
        String result = playlists.stream()
                .sorted(Comparator.comparingInt((Playlist p) -> p.getSongs().stream()
                        .mapToInt(Song::getLikes).sum()).reversed())
                .limit(limit)
                .map(Playlist::getName)
                .collect(Collectors.joining(", "));
        return result.isEmpty() ? "[]" : "[" + result + "]";
    }
}
