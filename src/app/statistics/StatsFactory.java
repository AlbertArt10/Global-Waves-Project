package app.statistics;

/**
 * Factory pentru crearea de instanțe ale obiectelor StatsTemplate, bazate pe tipul de utilizator.
 */
public final class StatsFactory {
    // Constructor privat pentru a preveni instantierea
    private StatsFactory() {
    }

    /**
     * Creează și returnează o instanță a unei clase care implementează StatsTemplate,
     *            bazată pe tipul de utilizator specificat.
     *
     * @param userType Tipul de utilizator pentru care se creează statistica.
     * @return O instanță a unei clase care implementează StatsTemplate corespunzătoare
     *              tipului de utilizator.
     * @throws IllegalStateException Dacă tipul de utilizator furnizat nu este recunoscut.
     */
    public static StatsTemplate createStatsTemplate(final String userType) {
        return switch (userType) {
            case "user" -> new UserStats();
            case "artist" -> new ArtistStats();
            case "host" -> new HostStats();
            default -> throw new IllegalStateException("Unexpected user type: " + userType);
        };
    }
}
