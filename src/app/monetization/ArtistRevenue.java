package app.monetization;

import lombok.Getter;

/**
 * Clasa care reprezintă veniturile unui artist, inclusiv veniturile din melodii, merchandising,
 *          și alte informații relevante precum cea mai profitabilă melodie.
 */
@Getter
public class ArtistRevenue {

    private final double songRevenue; // Veniturile artistului din melodii
    private final double merchRevenue; // Veniturile artistului din vânzările de merch
    private final int ranking; // Rangul artistului pe platformă bazat pe venituri
    private final String mostProfitableSong; // Melodia cea mai profitabilă a artistului

    public ArtistRevenue(final double songRevenue, final double merchRevenue, final int ranking,
                         final String mostProfitableSong) {
        this.songRevenue = songRevenue;
        this.merchRevenue = merchRevenue;
        this.ranking = ranking;
        this.mostProfitableSong = mostProfitableSong;
    }
}
