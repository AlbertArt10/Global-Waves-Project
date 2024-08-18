package app.user;

import app.notifications.Notification;
import app.pages.Page;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Content creator.
 */
@Getter
public abstract class ContentCreator extends UserAbstract {

    private String description;
    private Page page;
    private final List<Subscriber> subscribers = new ArrayList<>();

    /**
     * Instantiates a new Content creator.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public ContentCreator(final String username, final int age, final String city) {
        super(username, age, city);
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Sets page.
     *
     * @param page the page
     */
    public void setPage(final Page page) {
        this.page = page;
    }

    /**
     * Notifică toți abonații (subscribers) cu o notificare specifică.
     *
     * @param notification Notificarea care va fi trimisă abonaților.
     */
    public void notifySubscribers(final Notification notification) {
        // Parcurge fiecare abonat și trimite notificarea
        for (Subscriber subscriber : subscribers) {
            subscriber.update(notification);
        }
    }

    /**
     * Schimbă starea de abonare a unui abonat (Subscriber).
     *
     * @param subscriber Abonatul (Subscriber) a cărui stare de abonament va fi schimbată.
     * @return Boolean care indică noua stare a abonamentului:
     *         true dacă utilizatorul este acum abonat, false dacă este dezabonat.
     */
    public boolean toggleSubscription(final Subscriber subscriber) {
        // Verifică dacă utilizatorul este deja abonat la ContentCreator
        boolean isSubscribed = this.getSubscribers().contains(subscriber);

        if (isSubscribed) {
            // Dezabonează utilizatorul
            subscribers.remove(subscriber);
        } else {
            // Abonează utilizatorul
            subscribers.add(subscriber);
        }

        // Returnează noua stare a abonamentului
        return !isSubscribed;
    }
}
