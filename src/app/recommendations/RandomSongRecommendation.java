package app.recommendations;

import app.Admin;
import app.audio.Files.Song;
import app.user.User;

import java.util.List;
import java.util.Random;

/**
 * O implementare a interfeței RecommendationStrategy care generează recomandări de melodii
 *          în mod aleatoriu, bazându-se pe genul melodiei curent ascultate de utilizator.
 */
public class RandomSongRecommendation implements RecommendationStrategy {

    private final int minPasedTime = 30;

    /**
     * Generează o recomandare aleatorie de melodie pentru un utilizator, pe baza
     *           genului melodiei curent ascultate.
     *
     * @param user Utilizatorul pentru care se generează recomandarea.
     * @return true dacă o recomandare a fost generată și aplicată cu succes, altfel false.
     */
    @Override
    public boolean generateRecommendation(final User user) {
        Admin adminInstance = Admin.getInstance();
        Song currentSong = (Song) user.getPlayer().getCurrentAudioFile();

        // Verifică dacă melodia curentă a fost ascultată pentru cel puțin 30 de secunde
        if (currentSong != null && user.getPlayer().getPassedTime() >= minPasedTime) {
            String genre = currentSong.getGenre();
            List<Song> songsGenre = adminInstance.getSongsGenre(genre);

            // Dacă există melodii în genul curent, alege una aleatorie pentru recomandare
            if (!songsGenre.isEmpty()) {
                int passedTime = user.getPlayer().getPassedTime();
                Song recommendedSong = selectRandomSong(songsGenre, passedTime);
                user.getHomePage().setSongRecommendation(recommendedSong);
                user.setLastRecommendationType("songRecommendation");
                return true;
            }
        }
        return false; // nicio recomandare generată
    }

    /**
     * Selectează o melodie aleatorie dintr-o listă dată, folosind un seed bazat pe timpul ascultat
     *
     * @param songs Lista de melodii din care să fie aleasă melodia.
     * @param seed  Seed-ul pentru generatorul de numere aleatorii.
     * @return Melodia selectată aleatoriu sau null dacă lista este goală.
     */
    private Song selectRandomSong(final List<Song> songs, final int seed) {
        if (songs.isEmpty()) {
            return null;
        }

        Random random = new Random(seed);
        int randomIndex = random.nextInt(songs.size());
        return songs.get(randomIndex);
    }
}
