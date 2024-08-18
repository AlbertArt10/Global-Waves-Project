package app.recommendations;

/**
 * Factory pentru crearea de strategii de recomandare, bazate pe un tip specificat.
 */
public final class RecommendationFactory {

    // Constructor privat pentru a preveni instantierea
    private RecommendationFactory() {
    }

    /**
     * Returnează o instanță specifică a unei strategii de recomandare, bazată pe tipul furnizat.
     *
     * @param type Tipul de recomandare dorit
     * @return O instanță a clasei care implementează RecommendationStrategy
     *           corespunzătoare tipului specificat.
     */
    public static RecommendationStrategy getRecommendationStrategy(final String type) {
        return switch (type) {
            case "random_song" -> new RandomSongRecommendation();
            case "random_playlist" -> new RandomPlaylistRecommendation();
            case "fans_playlist" -> new FansPlaylistRecommendation();
            default -> null;
        };
    }
}
