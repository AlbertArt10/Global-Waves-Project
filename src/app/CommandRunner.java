package app;

import app.audio.Collections.AlbumOutput;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.PodcastOutput;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.Artist;
import app.monetization.ArtistRevenue;
import app.user.Host;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Command runner.
 */
public final class CommandRunner {
    /**
     * The Object mapper.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static Admin admin;

    /**
     * Update admin.
     */
    public static void updateAdmin() {
        admin = Admin.getInstance();
    }

    private CommandRunner() {
    }

    /**
     * Search object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode search(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        Filters filters = new Filters(commandInput.getFilters());
        String type = commandInput.getType();
        ArrayList<String> results = new ArrayList<>();
        String message = "%s is offline.".formatted(user.getUsername());

        if (user.isStatus()) {
            results = user.search(filters, type);
            message = "Search returned " + results.size() + " results";
        }

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        objectNode.put("results", OBJECT_MAPPER.valueToTree(results));

        return objectNode;
    }

    /**
     * Select object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode select(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());

        String message = user.select(commandInput.getItemNumber());

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Load object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode load(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.load();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Play pause object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode playPause(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.playPause();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Repeat object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode repeat(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.repeat();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Shuffle object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode shuffle(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        Integer seed = commandInput.getSeed();
        String message = user.shuffle(seed);

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Forward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode forward(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.forward();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Backward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode backward(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.backward();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Like object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode like(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.like();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Next object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode next(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.next();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Prev object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode prev(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.prev();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Create playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode createPlaylist(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.createPlaylist(commandInput.getPlaylistName(),
                                             commandInput.getTimestamp());

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add remove in playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addRemoveInPlaylist(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.addRemoveInPlaylist(commandInput.getPlaylistId());

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Switch visibility object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode switchVisibility(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.switchPlaylistVisibility(commandInput.getPlaylistId());

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show playlists object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showPlaylists(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        ArrayList<PlaylistOutput> playlists = user.showPlaylists();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Follow object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode follow(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.follow();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Status object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode status(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        PlayerStats stats = user.getPlayerStats();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("stats", OBJECT_MAPPER.valueToTree(stats));

        return objectNode;
    }

    /**
     * Show liked songs object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showLikedSongs(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        ArrayList<String> songs = user.showPreferredSongs();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets preferred genre.
     *
     * @param commandInput the command input
     * @return the preferred genre
     */
    public static ObjectNode getPreferredGenre(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String preferredGenre = user.getPreferredGenre();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(preferredGenre));

        return objectNode;
    }

    /**
     * Switch connection status object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode switchConnectionStatus(final CommandInput commandInput) {
        String message = admin.switchStatus(commandInput.getUsername());
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add user object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addUser(final CommandInput commandInput) {
        String message = admin.addNewUser(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Delete user object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode deleteUser(final CommandInput commandInput) {
        String message = admin.deleteUser(commandInput.getUsername());
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add album object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addAlbum(final CommandInput commandInput) {
        String message = admin.addAlbum(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove album object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removeAlbum(final CommandInput commandInput) {
        String message = admin.removeAlbum(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show albums object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showAlbums(final CommandInput commandInput) {
        Artist artist = admin.getArtist(commandInput.getUsername());
        ArrayList<AlbumOutput> albums = artist.showAlbums();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(albums));

        return objectNode;
    }

    /**
     * Add event object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addEvent(final CommandInput commandInput) {
        String message = admin.addEvent(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove event object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removeEvent(final CommandInput commandInput) {
        String message = admin.removeEvent(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add podcast object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addPodcast(final CommandInput commandInput) {
        String message = admin.addPodcast(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove podcast object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removePodcast(final CommandInput commandInput) {
        String message = admin.removePodcast(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show podcasts object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showPodcasts(final CommandInput commandInput) {
        Host host = admin.getHost(commandInput.getUsername());
        List<PodcastOutput> podcasts = host.getPodcasts().stream().map(PodcastOutput::new).toList();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(podcasts));

        return objectNode;
    }

    /**
     * Add merch object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addMerch(final CommandInput commandInput) {
        String message = admin.addMerch(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add announcement object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addAnnouncement(final CommandInput commandInput) {
        String message = admin.addAnnouncement(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove announcement object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removeAnnouncement(final CommandInput commandInput) {
        String message = admin.removeAnnouncement(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Gets online users.
     *
     * @param commandInput the command input
     * @return the online users
     */
    public static ObjectNode getOnlineUsers(final CommandInput commandInput) {
        List<String> onlineUsers = admin.getOnlineUsers();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(onlineUsers));

        return objectNode;
    }

    /**
     * Gets all users.
     *
     * @param commandInput the command input
     * @return the all users
     */
    public static ObjectNode getAllUsers(final CommandInput commandInput) {
        List<String> users = admin.getAllUsers();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(users));

        return objectNode;
    }

    /**
     * Change page object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode changePage(final CommandInput commandInput) {
        String message = admin.changePage(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Print current page object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode printCurrentPage(final CommandInput commandInput) {
        String message = admin.printCurrentPage(commandInput);
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Gets top 5 album list.
     *
     * @param commandInput the command input
     * @return the top 5 album list
     */
    public static ObjectNode getTop5AlbumList(final CommandInput commandInput) {
        List<String> albums = admin.getTop5AlbumList();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(albums));

        return objectNode;
    }

    /**
     * Gets top 5 artist list.
     *
     * @param commandInput the command input
     * @return the top 5 artist list
     */
    public static ObjectNode getTop5ArtistList(final CommandInput commandInput) {
        List<String> artists = admin.getTop5ArtistList();
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(artists));

        return objectNode;
    }

    /**
     * Gets top 5 songs.
     *
     * @param commandInput the command input
     * @return the top 5 songs
     */
    public static ObjectNode getTop5Songs(final CommandInput commandInput) {
        List<String> songs = admin.getTop5Songs();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets top 5 playlists.
     *
     * @param commandInput the command input
     * @return the top 5 playlists
     */
    public static ObjectNode getTop5Playlists(final CommandInput commandInput) {
        List<String> playlists = admin.getTop5Playlists();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", OBJECT_MAPPER.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Această metodă returnează statistici despre activitatea muzicală a utilizatorului,
     *          în funcție de tipul de utilizator (user, artist sau host).
     * Statisticile sunt calculate de la timestamp-ul 0 până la timestamp-ul curent.
     *
     * @param commandInput the command input
     * @return the objectNode care reprezintă statisticile user/artist/host-ului respectiv.
     */
    public static ObjectNode wrapped(final CommandInput commandInput) {
        ObjectNode resultNode = admin.wrapped(commandInput);

        // Creăm un nou ObjectNode pentru răspuns
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", "wrapped");
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        // Verificăm dacă resultNode conține doar mesajul
        if (resultNode.size() == 1 && resultNode.has("message")) {
            // Dacă există doar mesajul, adăugăm direct acest mesaj în objectNode
            objectNode.put("message", resultNode.get("message").asText());
        } else {
            // Altfel, includem resultNode sub cheia "result"
            objectNode.set("result", resultNode);
        }

        return objectNode;
    }

    /**
     * Procesează comanda pentru cumpărarea unei subscripții Premium de către un utilizator.
     *
     * @param commandInput the command input
     * @return the objectNode care conține detalii despre comanda efectuată, inclusiv
     *               numele user-ului, timestamp-ul și mesajul generat în urma executării comenzii.
     */
    public static ObjectNode buyPremium(final CommandInput commandInput) {
        // Obține obiectul utilizatorului pe baza numelui de utilizator din comandă
        User user = admin.getUser(commandInput.getUsername());

        // Execută operația de cumpărare a abonamentului Premium și obține mesajul rezultat
        String message = user.buyPremium();

        // Creează un nod JSON pentru a stoca și returna detaliile comenzii și rezultatul acesteia
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Procesează comanda pentru anularea abonamentului premium al unui utilizator.
     *
     * @param commandInput the command input
     * @return the objectNode care conține detalii despre comanda efectuată, inclusiv
     *               numele user-ului, timestamp-ul și mesajul generat în urma executării comenzii.
     */
    public static ObjectNode cancelPremium(final CommandInput commandInput) {
        // Obține obiectul user-ului pe baza numelui de utilizator specificat în comandă
        User user = admin.getUser(commandInput.getUsername());

        // Execută operația de anulare a abonamentului premium și obține mesajul rezultat
        String message = user.cancelPremium();

        // Creează un nod JSON pentru a stoca și returna detaliile comenzii și rezultatul acesteia
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Procesează comanda de adăugare a unei reclame în coada de redare a utilizatorului.
     *
     * @param commandInput Informațiile primite de la comandă.
     * @return the objectNode care conține detaliile despre comanda efectuată și
     *                 mesajul generat.
     */
    public static ObjectNode adBreak(final CommandInput commandInput) {
        // Obținerea user-ului pe baza numelui de utilizator din comandă
        User user = admin.getUser(commandInput.getUsername());
        String message;

        // Verificarea dacă utilizatorul există
        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.adBreak(commandInput.getPrice());
        }

        // Crearea răspunsului
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Procesează comanda de cumpărare a unui produs de merch (merchandise) de către un utilizator.
     *
     * @param commandInput the command input
     * @return the objectNode care reprezintă răspunsul procesat pentru comanda buyMerch.
     */
    public static ObjectNode buyMerch(final CommandInput commandInput) {
        // Obține user-ul bazat pe numele de utilizator furnizat în comandă
        User user = admin.getUser(commandInput.getUsername());
        String message;

        // Verifică dacă utilizatorul există
        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            // Încearcă să efectueze cumpărarea de merch și obține mesajul corespunzător
            message = user.buyMerch(commandInput.getName());
        }

        // Creează un obiect ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Procesează comanda de vizualizare a produselor de merch cumpărate de un utilizator.
     *
     * @param commandInput the command input
     * @return the objectNode care reprezintă răspunsul procesat pentru comanda de
     *               vizualizare merch.
     */
    public static ObjectNode seeMerch(final CommandInput commandInput) {
        // Obținerea user-ului pe baza numelui de utilizator din comandă
        User user = admin.getUser(commandInput.getUsername());

        // Creează un obiect ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        // Verifică dacă utilizatorul există
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername()
                        + " doesn't exist.");
        } else {
            // Adaugă lista de merch-uri cumpărate in result
            List<String> purchasedMerchNames = user.getPurchasedMerchNames();
            objectNode.set("result", OBJECT_MAPPER.valueToTree(purchasedMerchNames));
        }

        return objectNode;
    }

    /**
     * Procesează comanda "subscribe" primită de la un utilizator.
     * Această metodă permite unui utilizator să se aboneze sau să se dezaboneze
     *         de la un ContentCreator
     *
     * @param commandInput the command input
     * @return the objectNode care conține informații despre executarea comenzii,
     *               inclusiv un mesaj cu rezultatul operației.
     */
    public static ObjectNode subscribe(final CommandInput commandInput) {
        // Obținerea user-ului pe baza numelui de utilizator din comandă
        User user = admin.getUser(commandInput.getUsername());
        String message;

        // Verifică dacă utilizatorul există în sistem
        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            // Se procesează comanda de abonare/dezabonare
            message = user.subscribe();
        }

        // Creează un obiect ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Procesează comanda de a obține notificările unui utilizator.
     *
     * @param commandInput the command input
     * @return ObjectNode care include detaliile execuției comenzii și notificările utilizatorului.
     */
    public static ObjectNode getNotifications(final CommandInput commandInput) {
        // Obține utilizatorul bazat pe numele de utilizator din comandă
        User user = admin.getUser(commandInput.getUsername());

        // Creează un nou ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        // Verifică dacă utilizatorul există în sistem
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername()
                      + " doesn't exist.");
        } else {
            objectNode.setAll(user.getNotifications());
        }

        return objectNode;
    }

    /**
     * Procesează comanda de navigare la următoarea pagină din istoricul de navigare
     *            al utilizatorului.
     *
     * @param commandInput the command input
     * @return ObjectNode care conține detalii despre execuția comenzii și un mesaj care descrie
     *                    rezultatul operației.
     */
    public static ObjectNode nextPage(final CommandInput commandInput) {
        // Obține utilizatorul pe baza numelui de utilizator
        User user = admin.getUser(commandInput.getUsername());

        // Creează un nou ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", "nextPage");
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        // Verifică dacă există o pagină următoare și navighează dacă este posibil
        if (user.goToNextPage()) {
            objectNode.put("message", "The user " + commandInput.getUsername()
                    + " has navigated successfully to the next page.");
        } else {
            objectNode.put("message", "There are no pages left to go forward.");
        }

        return objectNode;
    }

    /**
     * Procesează comanda de navigare la pagina anterioară din istoricul de navigare
     *            al utilizatorului.
     *
     * @param commandInput the command input
     * @return ObjectNode care conține detalii despre execuția comenzii și un mesaj care descrie
     *                    rezultatul operației.
     */
    public static ObjectNode previousPage(final CommandInput commandInput) {
        // Obține utilizatorul pe baza numelui de utilizator
        User user = admin.getUser(commandInput.getUsername());

        // Crează un nou ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", "previousPage");
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        // Verifică dacă există o pagină anterioară și navighează dacă este posibil
        if (user.goToPreviousPage()) {
            objectNode.put("message", "The user " + commandInput.getUsername()
                    + " has navigated successfully to the previous page.");
        } else {
            objectNode.put("message", "There are no pages left to go back.");
        }

        return objectNode;
    }

    /**
     * Actualizează recomandările pentru un utilizator specific.
     *
     * @param commandInput the command input
     * @return Un ObjectNode care conține detaliile comenzii și rezultatul
     *            actualizării recomandărilor.
     */
    public static ObjectNode updateRecommendations(final CommandInput commandInput) {
        // Apelează metoda de actualizare a recomandărilor
        String message = admin.updateRecommendations(commandInput);

        // Creează un nou ObjectNode pentru a stoca detaliile răspunsului
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Încarcă recomandările pentru un utilizator specific.
     *
     * @param commandInput the command input
     * @return Un ObjectNode care conține detaliile comenzii și mesajul rezultat.
     */
    public static ObjectNode loadRecommendations(final CommandInput commandInput) {
        User user = admin.getUser(commandInput.getUsername());
        String message = user.loadRecommendations();

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Procesează finalizarea programului și calculează veniturile artiștilor, generând
     *            un raport de monetizare.
     *
     * <p>Nu există un input specific pentru această comandă; raportul este generat automat
     *           la finalul fiecărei rulări.</p>
     *
     * @return Un ObjectNode ce conține veniturile calculate și alte informații relevante
     *          pentru fiecare artist.
     */
    public static ObjectNode endProgram() {
        // Calculează veniturile fiecărui artist
        Map<Artist, ArtistRevenue> artistRevenues = admin.calculateArtistRevenues();

        // Creează un ObjectNode pentru a stoca rezultatele finale
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();

        // Parcurge harta de venituri a artiștilor pentru a construi raportul
        for (Map.Entry<Artist, ArtistRevenue> entry : artistRevenues.entrySet()) {
            // Extrage numele artistului și ArtistRevenue asociat acestuia
            String artistName = entry.getKey().getUsername();
            ArtistRevenue revenue = entry.getValue();

            ObjectNode artistNode = OBJECT_MAPPER.createObjectNode();
            artistNode.put("merchRevenue", revenue.getMerchRevenue());
            artistNode.put("songRevenue", revenue.getSongRevenue());
            artistNode.put("ranking", revenue.getRanking());
            artistNode.put("mostProfitableSong", revenue.getMostProfitableSong());

            resultNode.set(artistName, artistNode);
        }

        // Creează un ObjectNode pentru a stoca comanda și rezultatul final
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("command", "endProgram");
        objectNode.set("result", resultNode);

        return objectNode;
    }
}
