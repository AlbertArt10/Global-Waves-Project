package app.pages.pageContent;

import lombok.Getter;

/**
 * The type Event.
 */
@Getter
public class Event {

    private String name;
    private String description;
    private String date;

    /**
     * Instantiates a new Event.
     *
     * @param name        the name
     * @param description the description
     * @param date        the date
     */
    public Event(final String name, final String description, final String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
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
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(final String date) {
        this.date = date;
    }
}
