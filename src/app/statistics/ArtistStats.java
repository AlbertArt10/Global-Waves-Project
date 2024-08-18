package app.statistics;

import app.audio.Files.Song;
import app.user.UserAbstract;
import app.user.Artist;
import fileio.input.CommandInput;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Clasa ArtistStats este responsabilă pentru calcularea și adăugarea statisticilor specifice
 *       unui artist într-un ObjectNode.
 * Această clasă extinde StatsTemplate și implementează metodele abstracte pentru a oferi
 *       statistici pentru fiecare artist.
 */
public class ArtistStats extends StatsTemplate {

    /**
     * Adaugă statisticile specifice unui artist într-un ObjectNode.
     *
     * @param resultNode Nodul ObjectNode în care se adaugă statisticile.
     * @param currentUser Utilizatorul curent, presupus a fi un artist.
     * @param commandInput Datele de intrare ale comenzii.
     */
    @Override
    protected void addSpecificStats(final ObjectNode resultNode, final UserAbstract currentUser,
                                    final CommandInput commandInput) {

        Artist artist = (Artist) currentUser;

        // Verifică dacă artistul are ascultători
        if (getListenersCount(artist) > 0) {
            // Adaugă statisticile specifice unui artist
            resultNode.set("topAlbums", createStatsNode(getTopAlbums(artist)));
            resultNode.set("topSongs", createStatsNode(getTopSongs(artist)));
            resultNode.set("topFans", OBJECT_MAPPER.valueToTree(getTopFans(artist)));
            resultNode.put("listeners", getListenersCount(artist));
        }
    }

    /**
     * Verifică dacă există date statistice de afișat pentru un artist specific.
     *
     * @param currentUser Utilizatorul curent, presupus a fi un artist.
     * @return true dacă artistul are date statistice relevante, altfel false.
     */
    @Override
    protected boolean hasDataToDisplay(final UserAbstract currentUser) {

        Artist artist = (Artist) currentUser;

        // Verifică dacă artistul are ascultători
        if (getListenersCount(artist) > 0) {
            return true;
        }

        return false;
    }

    /**
     * Calculează și returnează o hartă a albumelor și numărului lor de ascultări
     *            pentru artistul curent.
     *
     * @param artist Artist-ul pentru care se calculeaza top-ul albumelor acestuia.
     * @return O hartă sortată care conține perechi cheie-valoare,
     *         unde cheia este numele albumului și valoarea este numărul total de ascultări.
     */
    public Map<String, Integer> getTopAlbums(final Artist artist) {
        // Inițializează o hartă pentru a stoca albumele și numărul lor de ascultări
        Map<String, Integer> topAlbums = new HashMap<>();

        // Listă pentru a stoca toate melodiile artistului
        List<Song> allArtistSongs = new ArrayList<>();

        // Obtine toate melodiile artistului  curent
        for (Song song : getAllSongs()) {
            if (song.getArtist().equals(artist.getUsername())) {
                allArtistSongs.add(song);
            }
        }

        // Parcurge toate melodiile artisului
        for (Song song : allArtistSongs) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator
            Map<String, Integer> listens = song.getUserListenCounts();

            // Calculează numărul total de ascultări ale melodiei
            int listenCount = listens.values().stream().mapToInt(Integer::intValue).sum();

            // Adaugă numărul de ascultări la totalul albumului dacă acesta este pozitiv
            if (listenCount > 0) {
                topAlbums.merge(song.getAlbum(), listenCount, Integer::sum);
            }
        }

        // Sortează harta în funcție de numărul de ascultări, descrescător
        // În caz de egalitate, sortează după numele albumului
        return sortAndLimit(topAlbums);
    }

    /**
     * Calculează și returnează o hartă a melodiilor artistului și numărului lor de ascultări.
     *
     * @param artist Artist-ul pentru care se calculeaza top-ul melodiilor acestuia.
     * @return O hartă sortată care conține perechi cheie-valoare,
     *         unde cheia este numele melodiei și valoarea este numărul total de ascultări.
     */
    public Map<String, Integer> getTopSongs(final Artist artist) {
        // Map pentru a stoca numărul de ascultări pentru fiecare melodie
        Map<String, Integer> topSongs = new HashMap<>();

        // Listă pentru a stoca toate melodiile artistului
        List<Song> allArtistSongs = new ArrayList<>();

        // Obtine toate melodiile artistului curent
        for (Song song : getAllSongs()) {
            if (song.getArtist().equals(artist.getUsername())) {
                allArtistSongs.add(song);
            }
        }

        for (Song song : allArtistSongs) {
            // Obține numărul de ascultări pentru fiecare melodie
            int listenCount = song.getListenCount();

            // Adaugă sau actualizează numărul de ascultări în map
            topSongs.merge(song.getName(), listenCount, Integer::sum);
        }


        // Sortează harta în funcție de numărul de ascultări, descrescător
        // În caz de egalitate, sortează după numele melodiei
        return sortAndLimit(topSongs);
    }

    /**
     * Calculează și returnează o listă a celor mai activi fani ai unui artist, bazată
     *            pe numărul de ascultări.
     *
     * @param artist Artist-ul pentru care se calculeaza top-ul fanilor acestuia.
     * @return O listă String care conține topul utilizatorilor
     */
    public List<String> getTopFans(final Artist artist) {
        // Map pentru a stoca numărul de ascultări pentru fiecare fan
        Map<String, Integer> topFanListens = new HashMap<>();

        // Listă pentru a stoca toate melodiile artistului
        List<Song> allArtistSongs = new ArrayList<>();

        // Obtine toate melodiile artistului  curent
        for (Song song : getAllSongs()) {
            if (song.getArtist().equals(artist.getUsername())) {
                allArtistSongs.add(song);
            }
        }

        // Parcurgem toate melodiile artistului
        for (Song song : allArtistSongs) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator al melodiei curente
            Map<String, Integer> listens = song.getUserListenCounts();

            // Adaugă sau actualizează numărul de ascultări pentru fiecare utilizator
            for (Map.Entry<String, Integer> entry : listens.entrySet()) {
                topFanListens.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        // Sortăm fanii în funcție de numărul total de ascultări și extragem numele lor într-o listă
        return new ArrayList<>(sortAndLimit(topFanListens).keySet());
    }

    /**
     * Calculează și returnează numărul total de ascultători unici ai unui artist.
     *
     * @param artist Artist-ul pentru care se calculeaza nr. total de ascultători unici.
     * @return Numărul total de ascultători unici ai artistului curent.
     */
    public int getListenersCount(final Artist artist) {
        // Set pentru a stoca ascultătorii unici ai artistului
        Set<String> uniqueListeners = new HashSet<>();

        // Listă pentru a stoca toate melodiile artistului
        List<Song> allArtistSongs = new ArrayList<>();

        // Obtine toate melodiile artistului  curent
        for (Song song : getAllSongs()) {
            if (song.getArtist().equals(artist.getUsername())) {
                allArtistSongs.add(song);
            }
        }

        // Parcurgem toate melodiile artistului
        for (Song song : allArtistSongs) {
            // Obține setul de ascultători unici pentru fiecare melodie
            Set<String> listeners = song.getUniqueListeners();

            // Adaugă toți ascultătorii unici în setul global
            uniqueListeners.addAll(listeners);
        }

        // Returnează numărul total de ascultători unici
        return uniqueListeners.size();
    }
}
