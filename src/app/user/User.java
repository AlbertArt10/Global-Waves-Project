package app.user;

import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.monetization.RevenueService;
import app.notifications.Notification;
import app.pages.Page;
import app.pages.HomePage;
import app.pages.LikedContentPage;
import app.pages.ArtistPage;
import app.pages.HostPage;
import app.pages.PageMemento;

import java.util.stream.Collectors;

import app.pages.pageContent.Merchandise;
import app.player.Player;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import app.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type User.
 */
@Getter
public final class User extends UserAbstract implements Subscriber {
    private ArrayList<Playlist> playlists;
    private ArrayList<Song> likedSongs;
    private ArrayList<Playlist> followedPlaylists;
    private final Player player;
    private boolean status;
    private final SearchBar searchBar;
    private boolean lastSearched;
    @Setter
    private Page currentPage;
    @Setter
    private HomePage homePage;
    @Setter
    private LikedContentPage likedContentPage;
    private boolean isPremium;
    // Lista pentru a urmări melodiile ascultate pe modul Premium
    private List<Song> songsListenedPremium = new ArrayList<>();
    // Lista pentru a urmări melodiile ascultate între ad break-uri
    private List<Song> songsListenedFree = new ArrayList<>();
    // Lista de merch-uri cumpărate de utilizator.
    private final ArrayList<Merchandise> purchasedMerch = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private PageMemento pageMemento = new PageMemento();
    @Setter
    private String lastRecommendationType;
    private RevenueService revenueService;


    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        super(username, age, city);
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        revenueService = new RevenueService(0);
        lastSearched = false;
        status = true;
        isPremium = false;

        homePage = new HomePage(this);
        currentPage = homePage;
        likedContentPage = new LikedContentPage(this);
        pageMemento.addPage(homePage);
    }

    @Override
    public String userType() {
        return "user";
    }

    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
        searchBar.clearSelection();
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();

        if (type.equals("artist") || type.equals("host")) {
            List<ContentCreator> contentCreatorsEntries =
            searchBar.searchContentCreator(filters, type);

            for (ContentCreator contentCreator : contentCreatorsEntries) {
                results.add(contentCreator.getUsername());
            }
        } else {
            List<LibraryEntry> libraryEntries = searchBar.search(filters, type);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
        }
        return results;
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;

        if (searchBar.getLastSearchType().equals("artist")
            || searchBar.getLastSearchType().equals("host")) {
            ContentCreator selected = searchBar.selectContentCreator(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            currentPage = selected.getPage();
            return "Successfully selected %s's page.".formatted(selected.getUsername());
        } else {
            LibraryEntry selected = searchBar.select(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            return "Successfully selected %s.".formatted(selected.getName());
        }
    }

    /**
     * Procesează și redă un fișier audio specificat, gestionând logistica de redare și de urmărire.
     *
     * @param audioFileEntry Intrarea bibliotecii care reprezintă sursa audio
     * @param sourceType Tipul sursei audio
     */
    private void processAndPlayAudioFile(final LibraryEntry audioFileEntry,
                                         final String sourceType) {

        player.setSource(audioFileEntry, sourceType);
        player.getSource().resetNextAdBreak();

        player.pause();

        // Obține melodia/episodul curent din player
        AudioFile audioFile = player.getCurrentAudioFile();

        // Incrementează numărul total de ascultări ale melodiei/episodului
        audioFile.incrementListenCount();

        // Incrementează numărul de ascultări ale fisierului audio pentru utilizatorul curent
        audioFile.incrementUserListenCount(this.getUsername());


        // Verifică dacă fișierul audio curent din player este o melodie
        if (player.getType().equals("song") || player.getType().equals("playlist")
                || player.getType().equals("album")) {
            // Transformă fișierul audio curent într-un obiect de tipul 'Song'
            Song currentSong = (Song) player.getCurrentAudioFile();

            // Obține numele artistului melodiei curente
            String artistName = currentSong.getArtist();

            // Verifică și adaugă artistul în lista de artiști dacă acesta nu este deja prezent
            Admin.getInstance().checkAndAddArtist(artistName);

            // Verifica daca user-ul este Premium și adaugă melodia curentă în lista pentru
            //          monetizarea Free sau Premium
            if (isPremium) {
                songsListenedPremium.add(currentSong);
            } else {
                songsListenedFree.add(currentSong);
            }
        }
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
        processAndPlayAudioFile(searchBar.getLastSelected(), searchBar.getLastSearchType());

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus;

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next(this.getUsername());

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, getUsername(), timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(getUsername())) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Switch status.
     */
    public void switchStatus() {
        status = !status;
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        if (!status) {
            return;
        }

        player.simulatePlayer(time, this.getUsername());
    }

    /**
     * Cumpără un abonament Premium pentru utilizator.
     *
     * @return Mesajul rezultat în urma comenzii
     */
    public String buyPremium() {
        // Verifică dacă utilizatorul este deja un utilizator Premium
        if (isPremium) {
            return getUsername() + " is already a premium user.";
        }

        // Setează statusul utilizatorului la Premium
        isPremium = true;

        // Returnează un mesaj de confirmare a cumpărării abonamentului Premium
        return getUsername() + " bought the subscription successfully.";
    }

    /**
     * Anulează abonamentul Premium al utilizatorului.
     *
     * @return Mesajul rezultat în urma comenzii.
     */
    public String cancelPremium() {
        // Verifică dacă utilizatorul este în prezent un utilizator Premium
        if (!isPremium) {
            return getUsername() + " is not a premium user.";
        }
        // Setează statusul utilizatorului la ne-Premium/normal
        isPremium = false;

        // Distribuie veniturile acumulate din ascultările utilizatorului Premium
        revenueService.revenueFromPremiumListens(this);

        // Returnează un mesaj de confirmare a anulării abonamentului Premium
        return getUsername() + " cancelled the subscription successfully.";
    }

    /**
     * Gestionează adăugarea unei reclame în coada de redare a utilizatorului.
     *
     * @param adPrice Prețul asociat reclamei care va fi redată.
     * @return String Un mesaj care indică dacă reclama a fost adăugată cu succes.
     */
    public String adBreak(final double adPrice) {
        // Verifică dacă utilizatorul are muzică în redare
        if (player.getCurrentAudioFile() == null) {
            return getUsername() + " is not playing any music.";
        } else {
            // Adaugă reclama în coada de redare
            player.getSource().setNextSongToAdBreak();

            // Setează venitul din reclama curentă
            revenueService.setAdPrice(adPrice);

            return "Ad inserted successfully.";
        }
    }

    /**
     * Permite utilizatorului să cumpere merchandise de pe pagina unui artist.
     *
     * @param merchName Numele produsului de merch pe care utilizatorul dorește să-l cumpere.
     * @return Un mesaj care indică rezultatul operațiunii de cumpărare.
     */
    public String buyMerch(final String merchName) {
        // Verifică dacă pagina curentă este o pagină de artist
        if (!(currentPage.getType().equals("artist"))) {
            return "Cannot buy merch from this page.";
        }

        // Obține artistul paginii curente și caută produsul de merch specificat
        Artist artist = ((ArtistPage) currentPage).getArtist();
        Merchandise merchandise = artist.getMerch().stream()
                .filter(m -> m.getName().equals(merchName))
                .findFirst()
                .orElse(null);

        // Verifică dacă produsul de merch există
        if (merchandise == null) {
            return "The merch " + merchName + " doesn't exist.";
        }

        // Asigură înregistrarea artistului în sistem dacă nu există deja
        Admin.getInstance().checkAndAddArtist(artist.getUsername());

        // Adaugă venitul generat de achiziție la totalul artistului
        artist.addMerchRevenue(merchandise.getPrice());

        // Adaugă produsul de merch în lista de merch cumpărat de utilizator
        purchasedMerch.add(merchandise);

        // Returnează un mesaj de confirmare
        return this.getUsername() + " has added new merch successfully.";
    }

    /**
     * Returnează numele tuturor produselor de merchandise cumpărate de utilizator.
     *
     * @return O listă de String-uri, fiecare reprezentând numele unui produs
     *             de merchandise cumpărat.
     */
    public List<String> getPurchasedMerchNames() {
        // Folosește un stream pentru a parcurge lista de merchandise cumpărat
        return this.purchasedMerch.stream()
                .map(Merchandise::getName)
                .collect(Collectors.toList());
    }

    /**
     * Implementează logica de actualizare a unui observator în momentul primirii unei notificări.
     *
     * @param notification Notificarea primită de la subiect (ContentCreator).
     */
    @Override
    public void update(final Notification notification) {
        // Adaugă notificarea primită la lista de notificări a utilizatorului
        notifications.add(notification);
    }

    /**
     * Gestionează abonarea sau dezabonarea unui utilizator la pagina unui ContentCreator
     *
     * @return Un mesaj care indică rezultatul operației de abonare/dezabonare.
     */
    public String subscribe() {
        // Verifică tipul paginii curente și obține referința către ContentCreator
        if (currentPage.getType().equals("artist") || currentPage.getType().equals("host")) {
            ContentCreator creator = currentPage.getType().equals("artist")
                    ? ((ArtistPage) currentPage).getArtist() : ((HostPage) currentPage).getHost();

            // Logica pentru abonare/dezabonare
            boolean isSubscribed = creator.toggleSubscription(this);

            // Comută starea de abonare și generează mesajul corespunzător
            String action = isSubscribed ? "subscribed to" : "unsubscribed from";
            return getUsername() + " " + action + " " + creator.getUsername() + " successfully.";
        } else {
            return "To subscribe you need to be on the page of an artist or host.";
        }
    }

    /**
     * Obține notificările utilizatorului și le returnează sub forma unui ObjectNode.
     * După accesarea notificărilor, acestea sunt șterse din lista internă a utilizatorului.
     *
     * @return ObjectNode care conține un array cu notificările utilizatorului.
     */
    public ObjectNode getNotifications() {
        // Creează un nou ObjectNode pentru a construi răspunsul
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        // Creează un ArrayNode pentru a stoca notificările
        ArrayNode notificationsArray = objectNode.putArray("notifications");

        // Adaugă fiecare notificare în ArrayNode sub forma unui ObjectNode
        for (Notification notification : notifications) {
            ObjectNode notificationNode = OBJECT_MAPPER.createObjectNode();
            notificationNode.put("name", notification.getName());
            notificationNode.put("description", notification.getDescription());
            notificationsArray.add(notificationNode);
        }

        // Șterge toate notificările utilizatorului
        notifications.clear();

        return objectNode;
    }

    /**
     * Schimbă pagina curentă a utilizatorului la o nouă pagină specificată.
     * @param newPage Noua pagină la care utilizatorul navighează.
     */
    public void changePage(final Page newPage) {
        // Adaugă noua pagină în istoricul de navigare al utilizatorului
        pageMemento.addPage(newPage);

        // Actualizează pagina curentă a utilizatorului la noua pagină
        this.currentPage = newPage;
    }

    /**
     * Navighează utilizatorul la pagina anterioară din istoricul său de navigare.
     *
     * @return boolean True dacă navigarea la pagina anterioară este posibilă și a fost realizată,
     *                 False dacă nu există o pagină anterioară în istoric.
     */
    public boolean goToPreviousPage() {
        // Verifică dacă este posibil să se navigheze la pagina anterioară
        if (pageMemento.canGoToPreviousPage()) {
            // Setează pagina curentă la pagina anterioară din istoric
            currentPage = pageMemento.getPreviousPage();
            return true;
        }
        // Returnează false dacă nu există o pagină anterioară în istoric
        return false;
    }

    /**
     * Navighează utilizatorul la pagina următoare din istoricul său de navigare.
     *
     * @return boolean True dacă navigarea la pagina următoare este posibilă și a fost realizată,
     *                 False dacă nu există o pagină următoare în istoric.
     */
    public boolean goToNextPage() {
        // Verifică dacă este posibil să se navigheze la pagina următoare
        if (pageMemento.canGoToNextPage()) {
            // Setează pagina curentă la pagina următoare din istoric
            currentPage = pageMemento.getNextPage();
            return true;
        }
        // Returnează false dacă nu există o pagină următoare în istoric
        return false;
    }

    /**
     * Încarcă și redă recomandările bazate pe ultimul tip de recomandare selectat de utilizator.
     *
     * @return Un mesaj care indică rezultatul procesului de încărcare a recomandărilor.
     */
    public String loadRecommendations() {
        // Verifică dacă utilizatorul este online
        if (!status) {
            return getUsername() + " is offline.";
        }

        // Verifică dacă există un tip de recomandare setat
        if (lastRecommendationType == null) {
            return "No recommendations available.";
        }

        // Procesează tipul de recomandare și inițiază redarea corespunzătoare
        switch (lastRecommendationType) {
            case "songRecommendation":
                Song songRecommendation = homePage.getSongRecommendation();
                if (songRecommendation != null) {
                    processAndPlayAudioFile(songRecommendation, "song");
                } else {
                    return "No recommendations available.";
                }
                break;
            case "playlistRecommendation":
                Playlist playlistRecommendation = homePage.getPlaylistRecommendation();
                if (playlistRecommendation != null) {
                    processAndPlayAudioFile(playlistRecommendation, "playlist");
                } else {
                    return "No recommendations available.";
                }
                break;
            default:
                return "Invalid recommendation type.";
        }

        return "Playback loaded successfully.";
    }
}
