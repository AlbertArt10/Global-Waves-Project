package app.user;

import lombok.Getter;

/**
 * The type User abstract.
 */
@Getter
public abstract class UserAbstract {

    private String username;
    private int age;
    private String city;

    /**
     * Instantiates a new User abstract.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public UserAbstract(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Sets age.
     *
     * @param age the age
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * User type string.
     *
     * @return the string
     */
    public abstract String userType();
}
