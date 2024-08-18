package app.recommendations;

import app.Admin;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.statistics.ArtistStats;
import app.user.Artist;
import app.user.User;


import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Implementare a interfeței {@code RecommendationStrategy} care generează recomandări
 *              de playlist-uri pe baza preferințelor fanilor unui anumit artist.
 */
public class FansPlaylistRecommendation implements RecommendationStrategy {

    private final int limit = 5;

    /**
     * Generează o recomandare de playlist bazată pe melodiile apreciate
     *           de cei mai mari fani ai artistuli a cărui melodie este în prezent
     *           ascultată de utilizator.
     *
     * @param user Utilizatorul pentru care se generează recomandarea.
     * @return true dacă o recomandare de playlist a fost generată și aplicată
     *         cu succes, altfel false.
     */
    @Override
    public boolean generateRecommendation(final User user) {
        // Obține statistici pentru artist și identifică fanii săi de top
        ArtistStats artistStats = new ArtistStats();
        Song currentSong = (Song) user.getPlayer().getCurrentAudioFile();
        Artist currentArtist = Admin.getInstance().getArtist(currentSong.getArtist());

        List<String> topFans = artistStats.getTopFans(currentArtist);
        Set<Song> uniqueSongs = new HashSet<>();

        // Parcurge lista de top fani și colectează melodiile apreciate de aceștia
        for (String fanUsername : topFans) {
            User fan = Admin.getInstance().getUser(fanUsername);
            List<Song> fanLikedSongs = fan.getLikedSongs()
                    .stream()
                    .sorted(Comparator.comparing(Song::getLikes).reversed())
                    .filter(uniqueSongs::add)
                    .limit(limit)
                    .collect(Collectors.toList());
            uniqueSongs.addAll(fanLikedSongs);
        }

        // Crează un playlist bazat pe melodiile apreciate de fani și îl recomandă utilizatorului
        if (!uniqueSongs.isEmpty()) {
            Playlist fanPlaylist = new Playlist(currentArtist.getUsername()
                    + " Fan Club recommendations", user.getUsername());
            fanPlaylist.setSongs(new ArrayList<>(uniqueSongs));
            user.getHomePage().setPlaylistRecommendation(fanPlaylist);
            user.setLastRecommendationType("playlistRecommendation");
            return true; // Nicio recomandare generată
        }
        return false;
    }
}
