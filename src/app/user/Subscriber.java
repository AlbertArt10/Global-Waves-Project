package app.user;

import app.notifications.Notification;

/**
 * Interfața Subscriber este utilizată în pattern-ul Observer pentru a defini un observator.
 * Această interfață permite subiectului să notifice toți observatorii abonați, fără a necesita
 *         cunoașterea detaliilor specifice ale fiecărui observator.
 */
public interface Subscriber {

    /**
     * Actualizează starea abonatului pe baza unei notificări primite de la subiect.
     * Această metodă este apelată de subiect pentru a trimite notificări observatorilor săi.
     *
     * @param notification Notificarea primită de la subiect.
     */
    void update(Notification notification);
}
