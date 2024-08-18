package app.recommendations;

import app.Admin;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * O implementare a interfeței RecommendationStrategy care generează recomandări de playlist-uri
 *        în mod aleatoriu, bazându-se pe genurile muzicale preferate de utilizator.
 */
public class RandomPlaylistRecommendation implements RecommendationStrategy {

    private final int limit = 3;

    /**
     * Generează o recomandare de playlist pentru un utilizator, bazându-se pe
     *          genurile muzicale preferate ale acestuia.
     *
     * @param user Utilizatorul pentru care se generează recomandarea.
     * @return true dacă o recomandare de playlist a fost generată și aplicată
     *              cu succes, altfel false.
     */
    @Override
    public boolean generateRecommendation(final User user) {
        Map<String, Integer> genreCount = calculateGenreCount(user);

        // Selectarea melodiilor pentru fiecare gen preferat
        ArrayList<Song> playlistSongs = new ArrayList<>();
        int[] limits = {5, 3, 2}; // Limitele pentru numărul de melodii din fiecare gen
        genreCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .forEach(entry -> {
                    String genre = entry.getKey();
                    int resultLimit = limits[playlistSongs.size() / 3];
                    List<Song> genreSongs = selectTopSongsByGenre(genre, resultLimit);
                    playlistSongs.addAll(genreSongs);
                });

        // Crearea și adăugarea playlistului recomandat
        if (!playlistSongs.isEmpty()) {

            Playlist newPlaylist = new Playlist(user.getUsername()
                    + "'s recommendations", user.getUsername());
            newPlaylist.setSongs(playlistSongs);
            user.getHomePage().setPlaylistRecommendation(newPlaylist);
            user.setLastRecommendationType("playlistRecommendation");
            return true;
        }
        return false; // Nicio recomandare generată
    }

    /**
     * Calculează numărul de genuri muzicale preferate de un utilizator,
     *            pe baza melodiilor apreciate și a playlist-urilor create sau urmărite.
     *
     * @param user Utilizatorul pentru care se face calculul.
     * @return O hartă cu genurile muzicale și frecvența lor.
     */
    private Map<String, Integer> calculateGenreCount(final User user) {
        Map<String, Integer> genreCount = new HashMap<>();

        // Calculul preferințelor de genuri bazate pe melodii apreciate și playlist-uri
        addGenresFromSongs(user.getLikedSongs(), genreCount);
        user.getPlaylists().forEach(playlist ->
                addGenresFromSongs(playlist.getSongs(), genreCount));
        user.getFollowedPlaylists().forEach(playlist ->
                addGenresFromSongs(playlist.getSongs(), genreCount));
        return genreCount;
    }

    /**
     * Adaugă genurile muzicale dintr-o listă de melodii într-o hartă de calcul al genurilor.
     *
     * @param songs Lista de melodii de la care se extrag genurile.
     * @param genreCount Harta în care se adaugă genurile extrase.
     */
    private void addGenresFromSongs(final List<Song> songs,
                                    final Map<String, Integer> genreCount) {
        for (Song song : songs) {
            genreCount.put(song.getGenre(), genreCount.getOrDefault(song.getGenre(), 0) + 1);
        }
    }

    /**
     * Selectează un număr limitat de melodii populare dintr-un anumit gen muzical.
     *
     * @param genre Genul muzical pentru care se selectează melodiile.
     * @param resultLimit Numărul maxim de melodii selectate.
     * @return O listă cu melodiile selectate din genul specificat.
     */
    private List<Song> selectTopSongsByGenre(final String genre, final int resultLimit) {
        Admin admin = Admin.getInstance();
        List<Song> songsOfGenre = admin.getSongsGenre(genre);
        return songsOfGenre.stream()
                .sorted(Comparator.comparing(Song::getLikes).reversed())
                .limit(resultLimit)
                .collect(Collectors.toList());
    }
}
