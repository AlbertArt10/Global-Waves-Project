package app.pages.pageContent;

import lombok.Getter;

/**
 * The type Merchandise.
 */
@Getter
public class Merchandise {

    private String name;
    private String description;
    private int price;

    /**
     * Instantiates a new Merchandise.
     *
     * @param name        the name
     * @param description the description
     * @param price       the price
     */
    public Merchandise(final String name, final String description, final int price) {
        this.name = name;
        this.description = description;
        this.price = price;
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
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(final int price) {
        this.price = price;
    }
}
