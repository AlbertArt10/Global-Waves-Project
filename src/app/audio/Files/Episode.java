package app.audio.Files;

import lombok.Getter;

@Getter
public final class Episode extends AudioFile {

    private final String host;
    private final String description;

    public Episode(final String name, final Integer duration, final String description,
                   final String host) {
        super(name, duration, host);
        this.host = host;
        this.description = description;
    }
}
