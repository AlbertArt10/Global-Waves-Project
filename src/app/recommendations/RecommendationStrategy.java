package app.recommendations;

import app.user.User;

/**
 * Interfața pentru strategiile de recomandare în sistemul de recomandări muzicale.
 */
public interface RecommendationStrategy {
    /**
     * Generează și aplică o recomandare pentru un utilizator dat.
     *
     * @param user Utilizatorul pentru care se generează recomandarea.
     * @return true dacă o recomandare a fost generată și aplicată cu succes, altfel false.
     */
    boolean generateRecommendation(User user);
}
