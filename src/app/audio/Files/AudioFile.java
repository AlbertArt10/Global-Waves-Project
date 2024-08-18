package app.audio.Files;

import app.audio.LibraryEntry;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class AudioFile extends LibraryEntry {

    private final String owner;
    private final Integer duration;
    // Nr. de ascultari al fișier-ului curent (in total)
    private Integer listenCount;
    // Hartă cu numărul de ascultări ale fișier-ului pentru fiecare user
    private final Map<String, Integer> userListenCounts = new HashMap<>();
    // Set cu utilizatorii unici care au ascultat fișierul audio
    private final Set<String> uniqueListeners = new HashSet<>();

    public AudioFile(final String name, final Integer duration, final String owner) {
        super(name);
        this.duration = duration;
        this.owner = owner;
        this.listenCount = 0;
    }

    /**
     * Metodă pentru a incrementa numărul de ascultări.
     */
    public void incrementListenCount() {
        this.listenCount++;
    }

    /**
     * Metodă pentru a incrementa numărul de ascultări de către un anumit utilizator
     * și a adăuga utilizatorul în setul de ascultători unici.
     *
     * @param username Numele de utilizator.
     */
    public void incrementUserListenCount(final String username) {
        userListenCounts.merge(username, 1, Integer::sum);
        uniqueListeners.add(username);
    }
}
