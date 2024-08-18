package app.player;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.user.User;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Player.
 */
public final class Player {
    private Enums.RepeatMode repeatMode;
    private boolean shuffle;
    private boolean paused;
    @Getter
    private PlayerSource source;
    @Getter
    private String type;
    private final int skipTime = 90;
    @Getter
    private int passedTime = 0;
    private final ArrayList<PodcastBookmark> bookmarks = new ArrayList<>();

    /**
     * Instantiates a new Player.
     */
    public Player() {
        this.repeatMode = Enums.RepeatMode.NO_REPEAT;
        this.paused = true;
    }

    /**
     * Stop.
     */
    public void stop() {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        repeatMode = Enums.RepeatMode.NO_REPEAT;
        paused = true;
        source = null;
        shuffle = false;
        passedTime = 0;
    }

    private void bookmarkPodcast() {
        if (source != null && source.getAudioFile() != null) {
            PodcastBookmark currentBookmark =
                    new PodcastBookmark(source.getAudioCollection().getName(),
                                        source.getIndex(),
                                        source.getDuration());
            bookmarks.removeIf(bookmark -> bookmark.getName().equals(currentBookmark.getName()));
            bookmarks.add(currentBookmark);
        }
    }

    /**
     * Create source player source.
     *
     * @param type      the type
     * @param entry     the entry
     * @param bookmarks the bookmarks
     * @return the player source
     */
    public static PlayerSource createSource(final String type,
                                            final LibraryEntry entry,
                                            final List<PodcastBookmark> bookmarks) {
        if ("song".equals(type)) {
            return new PlayerSource(Enums.PlayerSourceType.LIBRARY, (AudioFile) entry);
        } else if ("playlist".equals(type)) {
            return new PlayerSource(Enums.PlayerSourceType.PLAYLIST, (AudioCollection) entry);
        } else if ("podcast".equals(type)) {
            return createPodcastSource((AudioCollection) entry, bookmarks);
        } else if ("album".equals(type)) {
            return new PlayerSource(Enums.PlayerSourceType.ALBUM, (AudioCollection) entry);
        }

        return null;
    }

    private static PlayerSource createPodcastSource(final AudioCollection collection,
                                                    final List<PodcastBookmark> bookmarks) {
        for (PodcastBookmark bookmark : bookmarks) {
            if (bookmark.getName().equals(collection.getName())) {
                return new PlayerSource(Enums.PlayerSourceType.PODCAST, collection, bookmark);
            }
        }
        return new PlayerSource(Enums.PlayerSourceType.PODCAST, collection);
    }

    /**
     * Sets source.
     *
     * @param entry      the entry
     * @param sourceType the sourceType
     */
    public void setSource(final LibraryEntry entry, final String sourceType) {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        this.type = sourceType;
        this.source = createSource(sourceType, entry, bookmarks);
        this.repeatMode = Enums.RepeatMode.NO_REPEAT;
        this.shuffle = false;
        this.paused = true;
    }

    /**
     * Pause.
     */
    public void pause() {
        paused = !paused;
    }

    /**
     * Shuffle.
     *
     * @param seed the seed
     */
    public void shuffle(final Integer seed) {
        if (seed != null) {
            source.generateShuffleOrder(seed);
        }

        if (source.getType() == Enums.PlayerSourceType.PLAYLIST
            || source.getType() == Enums.PlayerSourceType.ALBUM) {
            shuffle = !shuffle;
            if (shuffle) {
                source.updateShuffleIndex();
            }
        }
    }

    /**
     * Repeat enums . repeat mode.
     *
     * @return the enums . repeat mode
     */
    public Enums.RepeatMode repeat() {
        if (repeatMode == Enums.RepeatMode.NO_REPEAT) {
            if (source.getType() == Enums.PlayerSourceType.LIBRARY) {
                repeatMode = Enums.RepeatMode.REPEAT_ONCE;
            } else {
                repeatMode = Enums.RepeatMode.REPEAT_ALL;
            }
        } else {
            if (repeatMode == Enums.RepeatMode.REPEAT_ONCE) {
                repeatMode = Enums.RepeatMode.REPEAT_INFINITE;
            } else {
                if (repeatMode == Enums.RepeatMode.REPEAT_ALL) {
                    repeatMode = Enums.RepeatMode.REPEAT_CURRENT_SONG;
                } else {
                    repeatMode = Enums.RepeatMode.NO_REPEAT;
                }
            }
        }

        return repeatMode;
    }

    /**
     * Simulate player.
     *
     * @param time the time
     * @param username Numele de utilizator care ascultă melodia
     */
    public void simulatePlayer(final int time, final String username) {
        int elapsedTime = time;
        if (!paused && source != null) {
            passedTime += time;
            while (elapsedTime >= source.getDuration()) {
                elapsedTime -= source.getDuration();
                next(username);
                // Verificăm daca urmează a fi redat o reclamă (Ad)
                if (source != null && source.getAudioFile().getName().equals("Ad Break")) {
                    // Dacă urmează o reclamă (Ad), distribuie venitul reclamei
                    User user = Admin.getInstance().getUser(username);
                    user.getRevenueService().revenueFromFreeListens(user);
                }
                if (paused) {
                    break;
                }
            }
            if (!paused) {
                source.skip(-elapsedTime);
            }
        }
    }

    /**
     * Next.
     *
     * @param username Numele de utilizator care ascultă melodia
     */
    public void next(final String username) {

        paused = source.setNextAudioFile(repeatMode, shuffle);
        passedTime = 0;

        if (repeatMode == Enums.RepeatMode.REPEAT_ONCE) {
            repeatMode = Enums.RepeatMode.NO_REPEAT;
        }

        if (source.getDuration() == 0 && paused) {
            stop();
        } else if (isValidAudioFile()) {
            processAudioFile(source.getAudioFile(), username);
        }
    }

    /**
     * Verifică dacă sursa audio curentă este validă și nu este o pauză publicitară.
     *
     * @return True dacă sursa audio este validă și nu este o pauză publicitară, altfel False.
     */
    private boolean isValidAudioFile() {
        return source != null && source.getDuration() != 0
                && !source.getAudioFile().getName().equals("Ad Break");
    }

    /**
     * Procesează fișierul audio curent, actualizând ascultările și adăugându-l
     *            în lista corespunzătoare utilizatorului.
     *
     * @param audioFile Fișierul audio care trebuie procesat.
     * @param username Numele de utilizator care ascultă fișierul audio.
     */
    private void processAudioFile(final AudioFile audioFile, final String username) {
        // Incrementează numărul total de ascultări ale melodiei/episodului
        audioFile.incrementListenCount();

        // Incrementează numărul de ascultări ale melodiei/episodului pentru user-ul curent
        audioFile.incrementUserListenCount(username);

        // Verifică dacă fișierul audio curent este o melodie
        if (this.getType().equals("song") || this.getType().equals("playlist")
                || this.getType().equals("album")) {
            // Converteste fișierul audio la obiectul de tip 'Song'
            Song currentSong = (Song) source.getAudioFile();

            // Obține user-ul pe baza numelui de utilizator
            User user = Admin.getInstance().getUser(username);

            // Verifica daca user-ul este Premium și adaugă melodia curentă în lista pentru
            //          monetizarea Free sau Premium
            if (user.isPremium()) {
                user.getSongsListenedPremium().add(currentSong);
            } else {
                user.getSongsListenedFree().add(currentSong);
            }
        }
    }

    /**
     * Prev.
     */
    public void prev() {
        source.setPrevAudioFile(shuffle);
        paused = false;
    }

    private void skip(final int duration) {
        source.skip(duration);
        paused = false;
    }

    /**
     * Skip next.
     */
    public void skipNext() {
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            skip(-skipTime);
        }
    }

    /**
     * Skip prev.
     */
    public void skipPrev() {
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            skip(skipTime);
        }
    }

    /**
     * Gets current audio file.
     *
     * @return the current audio file
     */
    public AudioFile getCurrentAudioFile() {
        if (source == null) {
            return null;
        }
        return source.getAudioFile();
    }

    /**
     * Gets current audio collection.
     *
     * @return the current audio collection
     */
    public AudioCollection getCurrentAudioCollection() {
        if (source == null) {
            return null;
        }
        return source.getAudioCollection();
    }

    /**
     * Gets paused.
     *
     * @return the paused
     */
    public boolean getPaused() {
        return paused;
    }

    /**
     * Gets shuffle.
     *
     * @return the shuffle
     */
    public boolean getShuffle() {
        return shuffle;
    }

    /**
     * Gets stats.
     *
     * @return the stats
     */
    public PlayerStats getStats() {
        String filename = "";
        int duration = 0;
        if (source != null && source.getAudioFile() != null) {
            filename = source.getAudioFile().getName();
            duration = source.getDuration();
        } else {
            stop();
        }

        return new PlayerStats(filename, duration, repeatMode, shuffle, paused);
    }
}
