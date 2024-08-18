package app.statistics;

import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.User;
import app.user.UserAbstract;
import fileio.input.CommandInput;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Clasa UserStats extinde StatsTemplate și implementează metodele pentru calculul și
 *        prezentarea statisticilor specifice utilizatorilor obișnuiți.
 */
public class UserStats extends StatsTemplate {

    /**
     * Adaugă statisticile specifice unui utilizator normal în nodul de rezultat JSON.
     * Include statistici precum artiștii, genurile, melodiile, albumele și episoadele preferate.
     *
     * @param resultNode Nodul JSON în care se adaugă statisticile.
     * @param currentUser Utilizatorul curent pentru care se calculează statisticile.
     * @param commandInput Informații de intrare legate de comandă.
     */
    @Override
    protected void addSpecificStats(final ObjectNode resultNode, final UserAbstract currentUser,
                                    final CommandInput commandInput) {

        User user = (User) currentUser;

        // Adaugă statisticile specifice unui User în resultNode
        resultNode.set("topArtists", createStatsNode(getTopArtists(user)));
        resultNode.set("topGenres", createStatsNode(getTopGenres(user)));
        resultNode.set("topSongs", createStatsNode(getTopSongs(user)));
        resultNode.set("topAlbums", createStatsNode(getTopAlbums(user)));
        resultNode.set("topEpisodes", createStatsNode(getTopEpisodes(user)));
    }

    /**
     * Verifică dacă utilizatorul are date statistice de afișat.
     *
     * @param currentUser Utilizatorul curent evaluat.
     * @return true dacă există date statistice relevante, altfel false.
     */
    @Override
    protected boolean hasDataToDisplay(final UserAbstract currentUser) {

        User user = (User) currentUser;

        // Verifică dacă utilizatorul are statistici de afisat
        if (!getTopArtists(user).isEmpty() || !getTopEpisodes(user).isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Calculează și returnează o hartă a artiștilor și numărului lor de ascultări de către un
     *            utilizator specific.
     *
     * @param user Utilizatorul curent pentru care se calculează numărul de ascultări ale
     *                      artiștilor.
     * @return O hartă sortată care conține perechi cheie-valoare, unde cheia este numele
     *             artistului și valoarea este numărul total de ascultări de către utilizator.
     */
    public Map<String, Integer> getTopArtists(final User user) {
        // Map pentru a stoca numărul de ascultări pentru fiecare artist
        Map<String, Integer> topArtists = new HashMap<>();

        // Iterăm prin toate melodiile și obținem numărul de ascultări pentru utilizatorul curent
        for (Song song : getAllSongs()) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator
            Map<String, Integer> listens = song.getUserListenCounts();

            // Verificăm dacă utilizatorul curent a ascultat melodia
            if (listens.containsKey(user.getUsername())) {
                // Obține numărul de ascultări pentru utilizatorul curent
                int count = listens.get(user.getUsername());
                // Adaugă sau actualizează numărul de ascultări pentru artistul melodiei
                topArtists.merge(song.getArtist(), count, Integer::sum);
            }
        }

        // Sortează artiștii în funcție de numărul total de ascultări și extrage topul
        return sortAndLimit(topArtists);
    }

    /**
     * Calculează și returnează o hartă a melodiilor și numărului lor de ascultări de către un
     *             utilizator specific.
     *
     * @param user Utilizatorul pentru care se calculează numărul de ascultări ale melodiilor.
     * @return O hartă sortată care conține perechi cheie-valoare, unde cheia este numele
     *           melodiei și valoarea este numărul total de ascultări de către utilizator.
     */
    public Map<String, Integer> getTopSongs(final User user) {
        // Map pentru a stoca numărul de ascultări pentru fiecare melodie
        Map<String, Integer> topSongs = new HashMap<>();

        // Iterăm prin toate melodiile și obținem numărul de ascultări pentru utilizatorul curent
        for (Song song : getAllSongs()) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator
            Map<String, Integer> listens = song.getUserListenCounts();

            // Verificăm dacă utilizatorul curent a ascultat melodia
            if (listens.containsKey(user.getUsername())) {
                // Obține numărul de ascultări pentru utilizatorul curent
                int count = listens.get(user.getUsername());

                // Adaugă sau actualizează numărul de ascultări pentru melodie în map
                topSongs.merge(song.getName(), count, Integer::sum);
            }
        }

        // Sortează melodiile în funcție de numărul total de ascultări și extrage topul
        return sortAndLimit(topSongs);
    }

    /**
     * Calculează și returnează o hartă a albumelor și numărului lor de ascultări de către
     *             un utilizator specific.
     *
     * @param user Utilizatorul pentru care se calculează numărul de ascultări ale albumelor.
     * @return O hartă sortată care conține perechi cheie-valoare, unde cheia este numele
     *           albumului și valoarea este numărul total de ascultări de către utilizator.
     */
    public Map<String, Integer> getTopAlbums(final User user) {
        // Map pentru a stoca numărul de ascultări pentru fiecare album
        Map<String, Integer> topAlbums = new HashMap<>();

        // Iterează prin toate melodiile
        for (Song song : getAllSongs()) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator
            Map<String, Integer> listens = song.getUserListenCounts();

            // Verifică dacă utilizatorul curent a ascultat melodia
            if (listens.containsKey(user.getUsername())) {
                // Obține numărul de ascultări pentru utilizatorul curent
                int count = listens.get(user.getUsername());

                // Obține numele albumului melodiei
                String albumName = song.getAlbum();

                // Adaugă sau actualizează numărul de ascultări pentru albumul respectiv
                topAlbums.merge(albumName, count, Integer::sum);
            }
        }

        // Sortează albumele în funcție de numărul total de ascultări și extrage topul
        return sortAndLimit(topAlbums);
    }

    /**
     * Calculează și returnează o hartă a genurilor muzicale și numărului lor de ascultări
     *             de către un utilizator specific.
     *
     * @param user User-ul pentru care se calculează numărul de ascultări ale genurilor muzicale.
     * @return O hartă sortată care conține perechi cheie-valoare, unde cheia este numele
     *           genului muzical și valoarea este numărul total de ascultări de către utilizator.
     */
    public Map<String, Integer> getTopGenres(final User user) {
        // Map pentru a stoca numărul de ascultări pentru fiecare gen muzical
        Map<String, Integer> topGenres = new HashMap<>();

        // Iterează prin toate melodiile
        for (Song song : getAllSongs()) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator
            Map<String, Integer> listens = song.getUserListenCounts();

            // Verifică dacă utilizatorul curent a ascultat melodia
            if (listens.containsKey(user.getUsername())) {
                // Obține numărul de ascultări pentru utilizatorul curent
                int count = listens.get(user.getUsername());

                // Obține genul muzical al melodiei
                String genre = song.getGenre();

                // Adaugă sau actualizează numărul de ascultări pentru genul muzical
                topGenres.merge(genre, count, Integer::sum);
            }
        }

        // Sortează genurile muzicale în funcție de numărul total de ascultări și extrage topul
        return sortAndLimit(topGenres);
    }

    /**
     * Calculează și returnează o hartă a episoadelor și numărului lor de ascultări
     *             de către un utilizator specific.
     *
     * @param user Utilizatorul pentru care se calculează numărul de ascultări ale episoadelor.
     * @return O hartă sortată care conține perechi cheie-valoare, unde cheia este
     *           numele episodului și valoarea este numărul total de ascultări de către utilizator.
     */
    public Map<String, Integer> getTopEpisodes(final User user) {
        // Map pentru a stoca numărul de ascultări pentru fiecare episod
        Map<String, Integer> topEpisodes = new HashMap<>();

        // Iterăm prin toate podcasturile si episoadele lor
        for (Podcast podcast : adminInstance.getPodcasts()) {
            for (Episode episode : podcast.getEpisodes()) {
                // Obține mapa cu numărul de ascultări pentru fiecare utilizator
                Map<String, Integer> listens = episode.getUserListenCounts();

                // Verificăm dacă utilizatorul curent a ascultat episodul
                if (listens.containsKey(user.getUsername())) {
                    // Obține numărul de ascultări pentru utilizatorul curent
                    int count = listens.get(user.getUsername());

                    // Adaugă sau actualizează numărul de ascultări pentru podcast în map
                    topEpisodes.merge(episode.getName(), count, Integer::sum);
                }
            }
        }

        // Sortează episoadele în funcție de numărul total de ascultări și extrage topul
        return sortAndLimit(topEpisodes);
    }
}
