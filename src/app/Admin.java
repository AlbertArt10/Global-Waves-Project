package app;

import app.audio.Collections.Album;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.notifications.Notification;
import app.player.Player;
import app.recommendations.RecommendationFactory;
import app.recommendations.RecommendationStrategy;
import app.statistics.StatsFactory;
import app.statistics.StatsTemplate;

import app.user.User;
import app.user.UserAbstract;
import app.user.Artist;
import app.user.Host;
import app.pages.pageContent.Event;
import app.pages.pageContent.Merchandise;
import app.pages.pageContent.Announcement;
import app.monetization.ArtistRevenue;
import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The type Admin.
 */
@Getter
public final class Admin {

    private final List<User> users = new ArrayList<>();
    private List<Artist> artists = new ArrayList<>();
    //Listă de artiști care au cel puțin un play sau vânzări pe platformă
    private Set<Artist> artistsListen = new HashSet<>();
    private List<Host> hosts = new ArrayList<>();
    private final List<Song> songs = new ArrayList<>();
    private List<Song> deletedSongs = new ArrayList<>();
    private final List<Podcast> podcasts = new ArrayList<>();
    private int timestamp = 0;
    private final int limit = 5;
    private final int dateStringLength = 10;
    private final int dateFormatSize = 3;
    private final int dateYearLowerLimit = 1900;
    private final int dateYearHigherLimit = 2023;
    private final int dateMonthLowerLimit = 1;
    private final int dateMonthHigherLimit = 12;
    private final int dateDayLowerLimit = 1;
    private final int dateDayHigherLimit = 31;
    private final int dateFebHigherLimit = 28;
    private final double hundred = 100.0; // Face parte din formula pt monetizare
    private static Admin instance;

    private Admin() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    /**
     * Reset instance.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public void setUsers(final List<UserInput> userInputList) {
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public void setSongs(final List<SongInput> songInputList) {
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }

    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public void setPodcasts(final List<PodcastInput> podcastInputList) {
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription(), podcastInput.getOwner()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Returnează o listă de melodii care corespund unui gen muzical specific.
     *
     * @param genre Genul muzical pentru care se caută melodiile.
     * @return Lista de melodii care aparțin genului specificat.
     */
    public List<Song> getSongsGenre(final String genre) {
        List<Song> songsOfGenre = new ArrayList<>();

        for (Song song : songs) {
            if (song.getGenre().equals(genre)) {
                songsOfGenre.add(song);
            }
        }

        return songsOfGenre;
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public List<Playlist> getPlaylists() {
        return users.stream()
                    .flatMap(user -> user.getPlaylists().stream())
                    .collect(Collectors.toList());
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public List<Album> getAlbums() {
        return artists.stream()
                      .flatMap(artist -> artist.getAlbums().stream())
                      .collect(Collectors.toList());
    }

    /**
     * Gets all users.
     *
     * @return the all users
     */
    public List<String> getAllUsers() {
        List<String> allUsers = new ArrayList<>();

        allUsers.addAll(users.stream().map(UserAbstract::getUsername).toList());
        allUsers.addAll(artists.stream().map(UserAbstract::getUsername).toList());
        allUsers.addAll(hosts.stream().map(UserAbstract::getUsername).toList());

        return allUsers;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public User getUser(final String username) {
        return users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Gets artist.
     *
     * @param username the username
     * @return the artist
     */
    public Artist getArtist(final String username) {
        return artists.stream()
                .filter(artist -> artist.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets host.
     *
     * @param username the username
     * @return the host
     */
    public Host getHost(final String username) {
        return hosts.stream()
                .filter(host -> host.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets song.
     *
     * @param songName the songName
     * @return the song
     */
    public Song getSong(final String songName) {
        return songs.stream()
                .filter(song -> song.getName().equals(songName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets episode.
     *
     * @param episodeName the episodeName
     * @return the episode
     */
    public Episode getEpisode(final String episodeName) {
        return podcasts.stream()
                .flatMap(podcast -> podcast.getEpisodes().stream())
                .filter(episode -> episode.getName().equals(episodeName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;

        if (elapsed == 0) {
            return;
        } else if (elapsed < 0) {
            throw new IllegalArgumentException("Invalid timestamp" + newTimestamp);
        }

        users.forEach(user -> user.simulateTime(elapsed));
    }

    private UserAbstract getAbstractUser(final String username) {
        ArrayList<UserAbstract> allUsers = new ArrayList<>();

        allUsers.addAll(users);
        allUsers.addAll(artists);
        allUsers.addAll(hosts);

        return allUsers.stream()
                       .filter(userPlatform -> userPlatform.getUsername().equals(username))
                       .findFirst()
                       .orElse(null);
    }

    /**
     * Add new user string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addNewUser(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String type = commandInput.getType();
        int age = commandInput.getAge();
        String city = commandInput.getCity();

        UserAbstract currentUser = getAbstractUser(username);
        if (currentUser != null) {
            return "The username %s is already taken.".formatted(username);
        }

        if (type.equals("user")) {
            users.add(new User(username, age, city));
        } else if (type.equals("artist")) {
            artists.add(new Artist(username, age, city));
        } else {
            hosts.add(new Host(username, age, city));
        }

        return "The username %s has been added successfully.".formatted(username);
    }

    /**
     * Delete user string.
     *
     * @param username the username
     * @return the string
     */
    public String deleteUser(final String username) {
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        }

        if (currentUser.userType().equals("user")) {
            return deleteNormalUser((User) currentUser);
        }

        if (currentUser.userType().equals("host")) {
            return deleteHost((Host) currentUser);
        }

        return deleteArtist((Artist) currentUser);
    }

    private String deleteNormalUser(final User user) {
        if (user.getPlaylists().stream().anyMatch(playlist -> users.stream().map(User::getPlayer)
                .filter(player -> player != user.getPlayer())
                .map(Player::getCurrentAudioCollection)
                .filter(Objects::nonNull)
                .anyMatch(collection -> collection == playlist))) {
            return "%s can't be deleted.".formatted(user.getUsername());
        }

        user.getLikedSongs().forEach(Song::dislike);
        user.getFollowedPlaylists().forEach(Playlist::decreaseFollowers);

        users.stream().filter(otherUser -> otherUser != user)
             .forEach(otherUser -> otherUser.getFollowedPlaylists()
                                            .removeAll(user.getPlaylists()));

        users.remove(user);
        return "%s was successfully deleted.".formatted(user.getUsername());
    }

    private String deleteHost(final Host host) {
        if (host.getPodcasts().stream().anyMatch(podcast -> getAudioCollectionsStream()
                .anyMatch(collection -> collection == podcast))
                || users.stream().anyMatch(user -> user.getCurrentPage() == host.getPage())) {
            return "%s can't be deleted.".formatted(host.getUsername());
        }

        host.getPodcasts().forEach(podcast -> podcasts.remove(podcast));
        hosts.remove(host);

        return "%s was successfully deleted.".formatted(host.getUsername());
    }

    private String deleteArtist(final Artist artist) {
        if (artist.getAlbums().stream().anyMatch(album -> album.getSongs().stream()
            .anyMatch(song -> getAudioFilesStream().anyMatch(audioFile -> audioFile == song))
            || getAudioCollectionsStream().anyMatch(collection -> collection == album))
            || users.stream().anyMatch(user -> user.getCurrentPage() == artist.getPage())) {
            return "%s can't be deleted.".formatted(artist.getUsername());
        }

        users.forEach(user -> artist.getAlbums().forEach(album -> album.getSongs().forEach(song -> {
            user.getLikedSongs().remove(song);
            user.getPlaylists().forEach(playlist -> playlist.removeSong(song));
        })));

        songs.removeAll(artist.getAllSongs());
        artists.remove(artist);
        return "%s was successfully deleted.".formatted(artist.getUsername());
    }

    /**
     * Add album string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addAlbum(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String albumName = commandInput.getName();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        if (currentArtist.getAlbums().stream()
            .anyMatch(album -> album.getName().equals(albumName))) {
            return "%s has another album with the same name.".formatted(username);
        }

        List<Song> newSongs = commandInput.getSongs().stream()
                                       .map(songInput -> new Song(songInput.getName(),
                                                                  songInput.getDuration(),
                                                                  albumName,
                                                                  songInput.getTags(),
                                                                  songInput.getLyrics(),
                                                                  songInput.getGenre(),
                                                                  songInput.getReleaseYear(),
                                                                  currentArtist.getUsername()))
                                       .toList();

        Set<String> songNames = new HashSet<>();
        if (!newSongs.stream().filter(song -> !songNames.add(song.getName()))
                  .collect(Collectors.toSet()).isEmpty()) {
            return "%s has the same song at least twice in this album.".formatted(username);
        }

        songs.addAll(newSongs);
        currentArtist.getAlbums().add(new Album(albumName,
                                                commandInput.getDescription(),
                                                username,
                                                newSongs,
                                                commandInput.getReleaseYear()));

        Notification notification = new Notification("New Album", "New Album from "
                                            + currentArtist.getUsername() + ".");
        currentArtist.notifySubscribers(notification);

        return "%s has added new album successfully.".formatted(username);
    }

    /**
     * Remove album string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removeAlbum(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String albumName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        Album searchedAlbum = currentArtist.getAlbum(albumName);
        if (searchedAlbum == null) {
            return "%s doesn't have an album with the given name.".formatted(username);
        }

        if (getAudioCollectionsStream().anyMatch(collection -> collection == searchedAlbum)) {
            return "%s can't delete this album.".formatted(username);
        }

        for (Song song : searchedAlbum.getSongs()) {
            if (getAudioCollectionsStream().anyMatch(collection -> collection.containsTrack(song))
                || getAudioFilesStream().anyMatch(audioFile -> audioFile == song)) {
                return "%s can't delete this album.".formatted(username);
            }
        }

        for (Song song: searchedAlbum.getSongs()) {
            users.forEach(user -> {
                user.getLikedSongs().remove(song);
                user.getPlaylists().forEach(playlist -> playlist.removeSong(song));
            });
            songs.remove(song);
            deletedSongs.add(song);
        }

        currentArtist.getAlbums().remove(searchedAlbum);
        return "%s deleted the album successfully.".formatted(username);
    }

    /**
     * Add podcast string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addPodcast(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String podcastName = commandInput.getName();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        if (currentHost.getPodcasts().stream()
            .anyMatch(podcast -> podcast.getName().equals(podcastName))) {
            return "%s has another podcast with the same name.".formatted(username);
        }

        List<Episode> episodes = commandInput.getEpisodes().stream()
                                             .map(episodeInput ->
                                                     new Episode(episodeInput.getName(),
                                                                 episodeInput.getDuration(),
                                                                 episodeInput.getDescription(),
                                                                 username))
                                             .collect(Collectors.toList());

        Set<String> episodeNames = new HashSet<>();
        if (!episodes.stream().filter(episode -> !episodeNames.add(episode.getName()))
                     .collect(Collectors.toSet()).isEmpty()) {
            return "%s has the same episode in this podcast.".formatted(username);
        }

        Podcast newPodcast = new Podcast(podcastName, username, episodes);
        currentHost.getPodcasts().add(newPodcast);
        podcasts.add(newPodcast);

        return "%s has added new podcast successfully.".formatted(username);
    }


    /**
     * Remove podcast string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removePodcast(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String podcastName = commandInput.getName();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        Podcast searchedPodcast = currentHost.getPodcast(podcastName);

        if (searchedPodcast == null) {
            return "%s doesn't have a podcast with the given name.".formatted(username);
        }

        if (getAudioCollectionsStream().anyMatch(collection -> collection == searchedPodcast)) {
            return "%s can't delete this podcast.".formatted(username);
        }

        currentHost.getPodcasts().remove(searchedPodcast);
        podcasts.remove(searchedPodcast);
        return "%s deleted the podcast successfully.".formatted(username);
    }

    /**
     * Add event string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addEvent(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String eventName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        if (currentArtist.getEvent(eventName) != null) {
            return "%s has another event with the same name.".formatted(username);
        }

        String date = commandInput.getDate();

        if (!checkDate(date)) {
            return "Event for %s does not have a valid date.".formatted(username);
        }

        currentArtist.getEvents().add(new Event(eventName,
                                                commandInput.getDescription(),
                                                commandInput.getDate()));

        Notification notification = new Notification("New Event", "New Event from "
                                               + currentArtist.getUsername() + ".");
        currentArtist.notifySubscribers(notification);

        return "%s has added new event successfully.".formatted(username);
    }

    /**
     * Remove event string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removeEvent(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String eventName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        Event searchedEvent = currentArtist.getEvent(eventName);
        if (searchedEvent == null) {
            return "%s doesn't have an event with the given name.".formatted(username);
        }

        currentArtist.getEvents().remove(searchedEvent);
        return "%s deleted the event successfully.".formatted(username);
    }

    private boolean checkDate(final String date) {
        if (date.length() != dateStringLength) {
            return false;
        }

        List<String> dateElements = Arrays.stream(date.split("-", dateFormatSize)).toList();

        if (dateElements.size() != dateFormatSize) {
            return false;
        }

        int day = Integer.parseInt(dateElements.get(0));
        int month = Integer.parseInt(dateElements.get(1));
        int year = Integer.parseInt(dateElements.get(2));

        if (day < dateDayLowerLimit
            || (month == 2 && day > dateFebHigherLimit)
            || day > dateDayHigherLimit
            || month < dateMonthLowerLimit || month > dateMonthHigherLimit
            || year < dateYearLowerLimit || year > dateYearHigherLimit) {
            return false;
        }

        return true;
    }

    /**
     * Add merch string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addMerch(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        if (currentArtist.getMerch().stream()
                         .anyMatch(merch -> merch.getName().equals(commandInput.getName()))) {
            return "%s has merchandise with the same name.".formatted(currentArtist.getUsername());
        } else if (commandInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        currentArtist.getMerch().add(new Merchandise(commandInput.getName(),
                                                     commandInput.getDescription(),
                                                     commandInput.getPrice()));

        Notification notification = new Notification("New Merchandise", "New Merchandise from "
                                               + currentArtist.getUsername() + ".");
        currentArtist.notifySubscribers(notification);

        return "%s has added new merchandise successfully.".formatted(username);
    }

    /**
     * Add announcement string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addAnnouncement(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String announcementName = commandInput.getName();
        String announcementDescription = commandInput.getDescription();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        Announcement searchedAnnouncement = currentHost.getAnnouncement(announcementName);
        if (searchedAnnouncement != null) {
            return "%s has already added an announcement with this name.";
        }

        currentHost.getAnnouncements().add(new Announcement(announcementName,
                                                            announcementDescription));
        return "%s has successfully added new announcement.".formatted(username);
    }

    /**
     * Remove announcement string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removeAnnouncement(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String announcementName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        Announcement searchAnnouncement = currentHost.getAnnouncement(announcementName);
        if (searchAnnouncement == null) {
            return "%s has no announcement with the given name.".formatted(username);
        }

        currentHost.getAnnouncements().remove(searchAnnouncement);
        return "%s has successfully deleted the announcement.".formatted(username);
    }

    /**
     * Change page string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String changePage(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String nextPage = commandInput.getNextPage();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("user")) {
            return "%s is not a normal user.".formatted(username);
        }

        User user = (User) currentUser;
        if (!user.isStatus()) {
            return "%s is offline.".formatted(user.getUsername());
        }

        switch (nextPage) {
            case "Home" -> user.changePage(user.getHomePage());
            case "LikedContent" -> user.changePage(user.getLikedContentPage());
            case "Artist" -> {
                Song song = (Song) user.getPlayer().getCurrentAudioFile();
                Artist artist = getArtist(song.getArtist());
                user.changePage(artist.getPage());
            }
            case "Host" -> {
                Episode episode = (Episode) user.getPlayer().getCurrentAudioFile();
                Host host = getHost(episode.getHost());
                user.changePage(host.getPage());
            }
            default -> {
                return "%s is trying to access a non-existent page.".formatted(username);
            }
        }

        // Resetează istoricul de navigare printre pagini
        user.getPageMemento().resetForwardHistory();

        return "%s accessed %s successfully.".formatted(username, nextPage);
    }

    /**
     * Print current page string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String printCurrentPage(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("user")) {
            return "%s is not a normal user.".formatted(username);
        }

        User user = (User) currentUser;
        if (!user.isStatus()) {
            return "%s is offline.".formatted(user.getUsername());
        }

        return user.getCurrentPage().printCurrentPage();
    }

    /**
     * Switch status string.
     *
     * @param username the username
     * @return the string
     */
    public String switchStatus(final String username) {
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        }

        if (currentUser.userType().equals("user")) {
            ((User) currentUser).switchStatus();
            return username + " has changed status successfully.";
        } else {
            return username + " is not a normal user.";
        }
    }

    /**
     * Gets online users.
     *
     * @return the online users
     */
    public List<String> getOnlineUsers() {
        return users.stream().filter(User::isStatus).map(User::getUsername).toList();
    }

    private Stream<AudioCollection> getAudioCollectionsStream() {
        return users.stream().map(User::getPlayer)
                    .map(Player::getCurrentAudioCollection).filter(Objects::nonNull);
    }

    private Stream<AudioFile> getAudioFilesStream() {
        return users.stream().map(User::getPlayer)
                    .map(Player::getCurrentAudioFile).filter(Objects::nonNull);
    }

    /**
     * Gets top 5 album list.
     *
     * @return the top 5 album list
     */
    public List<String> getTop5AlbumList() {
        List<Album> albums = artists.stream().map(Artist::getAlbums)
                                    .flatMap(List::stream).toList();

        final Map<Album, Integer> albumLikes = new HashMap<>();
        albums.forEach(album -> albumLikes.put(album, album.getSongs().stream()
                                          .map(Song::getLikes).reduce(0, Integer::sum)));

        return albums.stream().sorted((o1, o2) -> {
            if ((int) albumLikes.get(o1) == albumLikes.get(o2)) {
                return o1.getName().compareTo(o2.getName());
            }
            return albumLikes.get(o2) - albumLikes.get(o1);
        }).limit(limit).map(Album::getName).toList();
    }

    /**
     * Gets top 5 artist list.
     *
     * @return the top 5 artist list
     */
    public List<String> getTop5ArtistList() {
        final Map<Artist, Integer> artistLikes = new HashMap<>();
        artists.forEach(artist -> artistLikes.put(artist, artist.getAllSongs().stream()
                                              .map(Song::getLikes).reduce(0, Integer::sum)));

        return artists.stream().sorted(Comparator.comparingInt(artistLikes::get).reversed())
                               .limit(limit).map(Artist::getUsername).toList();
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= limit) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= limit) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Verifică existența unui artist în sistem și îl adaugă dacă nu există.
     * Această metodă verifică dacă un artist cu numele dat este deja prezent în lista de artiști
     *         din Admin. Dacă artistul nu există, se creează un nou obiect artist și se adaugă
     *         atât în lista generală a artiștilor cât și în lista artiștilor cu melodii ascultate,
     *         care au cel putin un Play sau vânzări pe platforma.
     *
     * @param artistName Numele artistului care trebuie verificat și eventual adăugat.
     */
    public void checkAndAddArtist(final String artistName) {

        // Verifică dacă artistul este deja înregistrat
        Artist artist = getArtist(artistName);
        if (artist == null) {
            // Crează și adaugă un nou artist în lista de artiști dacă acesta nu există
            Artist newArtist = new Artist(artistName, 0, "");
            artists.add(newArtist);

            // Adaugă noul artistul în lista 'artistListens'
            artistsListen.add(newArtist);
        } else {
            // Adaugă artistul existent în lista 'artistListens'
            artistsListen.add(artist);
        }
    }

    /**
     * Calculează și returnează veniturile pentru fiecare artist.
     *
     * @return O hartă care asociază fiecare artist cu un obiect ArtistRevenue,
     *         conținând detaliile veniturilor și clasamentul fiecărui artist.
     */
    public Map<Artist, ArtistRevenue> calculateArtistRevenues() {
        Map<Artist, ArtistRevenue> artistRevenues = new LinkedHashMap<>();

        // Distribuie veniturile de la utilizatorii Premium
        for (User user : users) {
            if (user.isPremium()) {
                user.getRevenueService().revenueFromPremiumListens(user);
            }
        }

        // Sortează artiștii în funcție de veniturile totale, apoi alfabetic
        List<Artist> sortedArtists = new ArrayList<>(artistsListen);
        sortedArtists.sort((artist1, artist2) -> {
            double totalRevenue1 = artist1.getSongRevenue() + artist1.getMerchRevenue();
            double totalRevenue2 = artist2.getSongRevenue() + artist2.getMerchRevenue();

            double revenueDifference = totalRevenue2 - totalRevenue1;
            if (revenueDifference == 0) {
                return artist1.getUsername().compareTo(artist2.getUsername());
            }
            return (int) Math.signum(revenueDifference);
        });

        // Construiește harta de venituri a artiștilor
        int ranking = 1;
        for (Artist artist : sortedArtists) {
            double songRevenue = Math.round(artist.getSongRevenue() * hundred) / hundred;
            double merchRevenue = Math.round(artist.getMerchRevenue() * hundred) / hundred;
            String mostProfitableSong = artist.determineMostProfitableSong();

            ArtistRevenue revenue = new ArtistRevenue(songRevenue, merchRevenue, ranking,
                                                   mostProfitableSong);
            artistRevenues.put(artist, revenue);
            ranking++;
        }

        return artistRevenues;
    }

    /**
     * Această metodă returnează statistici despre activitatea muzicală a utilizatorului
     *         în funcție de tipul de utilizator (user, artist sau host).
     * Statisticile sunt calculate de la timestamp-ul 0 până la timestamp-ul curent.
     *
     * @param commandInput the command input
     * @return the objectNode care reprezintă statisticile user/artist/host-ului respectiv
     */
    public ObjectNode wrapped(final CommandInput commandInput) {
        // Obține utilizatorul curent în funcție de numele de utilizator din input
        UserAbstract currentUser = getAbstractUser(commandInput.getUsername());

        // Selectează strategia de calcul a statisticilor în funcție de tipul utilizatorului
        StatsTemplate statsTemplate = StatsFactory.createStatsTemplate(currentUser.userType());

        // Calculează și returnează statisticile
        return statsTemplate.calculateStats(currentUser, commandInput);
    }

    /**
     * Actualizează recomandările pentru un utilizator specificat.
     *
     * @param commandInput Informațiile primite de la comandă.
     * @return Un string care indică rezultatul operațiunii de actualizare a recomandărilor.
     */
    public String updateRecommendations(final CommandInput commandInput) {
        // Obține tipul de recomandare și numele de utilizator din input
        String recommendationType = commandInput.getRecommendationType();
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        // Verifică dacă utilizatorul există și dacă este de tipul 'user'
        if (currentUser == null) {
            return "The username " + username + " doesn't exist.";
        } else if (!currentUser.userType().equals("user")) {
            return username + " is not a normal user.";
        }

        User user = (User) currentUser;

        // Obține strategia de recomandare specifică tipului dorit
        RecommendationStrategy strategy = RecommendationFactory
                .getRecommendationStrategy(recommendationType);

        // Generează recomandările și verifică dacă au fost actualizate cu succes
        boolean recommendationsUpdated = strategy.generateRecommendation(user);

        // Returnează mesajul corespunzător rezultatului
        if (recommendationsUpdated) {
            return "The recommendations for user " + username + " have been updated successfully.";
        } else {
            return "No new recommendations were found";
        }
    }
}
