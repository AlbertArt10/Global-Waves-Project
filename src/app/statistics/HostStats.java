package app.statistics;

import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.user.UserAbstract;
import app.user.Host;
import fileio.input.CommandInput;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Clasa HostStats este responsabilă pentru calcularea și adăugarea statisticilor specifice
 *          unui Host într-un ObjectNode.
 * Această clasă extinde StatsTemplate și implementează metodele abstracte pentru a oferi
 *          statistici pentru fiecare Host.
 */
public class HostStats extends StatsTemplate {

    /**
     * Adaugă statisticile specifice unui Host într-un ObjectNode.
     *
     * @param resultNode Nodul ObjectNode în care se adaugă statisticile.
     * @param currentUser Utilizatorul curent, presupus a fi un Host.
     * @param commandInput Datele de intrare ale comenzii.
     */
    @Override
    protected void addSpecificStats(final ObjectNode resultNode, final UserAbstract currentUser,
                                    final CommandInput commandInput) {

        Host host = (Host) currentUser;

        // Verifică dacă Host-ul are ascultători
        if (getListenersCount(host) > 0) {
            // Adaugă statisticile specifice unui Host
            resultNode.set("topEpisodes", createStatsNode(getTopEpisodes(host)));
            resultNode.put("listeners", getListenersCount(host));
        }
    }

    /**
     * Verifică dacă există date statistice relevante de afișat pentru Host-ul curent.
     *
     * @param currentUser Utilizatorul curent, presupus a fi un Host.
     * @return true dacă gazda de podcast are date statistice de afișat, altfel false.
     */
    @Override
    protected boolean hasDataToDisplay(final UserAbstract currentUser) {

        Host host = (Host) currentUser;

        // Verifică dacă Host-ul are ascultători
        if (getListenersCount(host) > 0) {
            return true;
        }

        return false;
    }

    /**
     * Calculează și returnează o hartă a episoadelor hostului și numărului lor de ascultări.
     *
     * @param host Host-ul pentru care se calculează topul episoadelor.
     * @return O hartă sortată care conține perechi cheie-valoare, unde cheia este numele
     *             episodului și valoarea este numărul total de ascultări.
     */
    public Map<String, Integer> getTopEpisodes(final Host host) {
        // Map pentru a stoca numărul de ascultări pentru fiecare episod
        Map<String, Integer> topEpisodes = new HashMap<>();

        // Iterează prin toate podcasturile
        for (Podcast podcast : adminInstance.getPodcasts()) {
            // Verificăm dacă Host-ul este owner pentru podcast-ul curent
            if (podcast.getOwner().equals(host.getUsername())) {

                for (Episode episode : podcast.getEpisodes()) {
                    // Obține numărul de ascultări pentru fiecare episod
                    int listenCount = episode.getListenCount();

                    // Include doar episoadele cu nr. pozitiv de ascultări
                    if (listenCount > 0) {
                        // Adaugă sau actualizează numărul de ascultări în map
                        topEpisodes.merge(episode.getName(), listenCount, Integer::sum);
                    }
                }
            }
        }

        // Sortează harta în funcție de numărul de ascultări, descrescător
        // În caz de egalitate, sortează după numele melodiei
        return sortAndLimit(topEpisodes);
    }

    /**
     * Calculează și returnează numărul total de ascultători unici ai unui host.
     *
     *
     * @param host Host-ul pentru care se calculeaza nr. total de ascultători unici.
     * @return Numărul total de ascultători unici ai hostului curent.
     */
    public int getListenersCount(final Host host) {
        // Set pentru a stoca ascultătorii unici ai hostului
        Set<String> uniqueListeners = new HashSet<>();

        // Set<String> uniqueListeners = host.getUniqueListeners();

        // Parcurgem toate podcasturile si episoadele
        for (Podcast podcast : adminInstance.getPodcasts()) {
            // Verificăm dacă Host-ul este owner pentru podcast-ul curent
            if (podcast.getOwner().equals(host.getUsername())) {
                for (Episode episode : podcast.getEpisodes()) {
                    // Obține setul de ascultători unici pentru fiecare episod
                    Set<String> listeners = episode.getUniqueListeners();

                    // Adaugă toți ascultătorii unici în setul global
                    uniqueListeners.addAll(listeners);
                }
            }
        }

        // Returnează numărul total de ascultători unici
        return uniqueListeners.size();
    }
}
