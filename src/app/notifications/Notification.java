package app.notifications;

import lombok.Getter;

/**
 * Clasa Notification reprezintă o notificare în sistem.
 * Aceasta este utilizată pentru a stoca informații despre evenimentele la care utilizatorii
 *         sunt abonați, cum ar fi adăugarea de noi albume sau podcast-uri de către ContentCreator.
 */
@Getter
public class Notification {

    private final String name;
    private final String description;

    /**
     * Inițializează o notificare nouă cu un nume și o descriere specificate.
     *
     * @param name        Numele notificării
     * @param description Descrierea detaliată a notificării
     */
    public Notification(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}
