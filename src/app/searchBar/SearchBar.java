package app.searchBar;


import app.Admin;
import app.audio.LibraryEntry;
import app.user.ContentCreator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static app.searchBar.FilterUtils.filterByAlbum;
import static app.searchBar.FilterUtils.filterByArtist;
import static app.searchBar.FilterUtils.filterByDescription;
import static app.searchBar.FilterUtils.filterByFollowers;
import static app.searchBar.FilterUtils.filterByGenre;
import static app.searchBar.FilterUtils.filterByLyrics;
import static app.searchBar.FilterUtils.filterByName;
import static app.searchBar.FilterUtils.filterByOwner;
import static app.searchBar.FilterUtils.filterByPlaylistVisibility;
import static app.searchBar.FilterUtils.filterByReleaseYear;
import static app.searchBar.FilterUtils.filterByTags;


/**
 * The type Search bar.
 */
public final class SearchBar {
    private static Admin admin;
    private List<LibraryEntry> results;
    private final String user;
    private static final Integer MAX_RESULTS = 5;
    @Getter
    private String lastSearchType;
    @Getter
    private LibraryEntry lastSelected;
    @Getter
    private List<ContentCreator> resultsContentCreator;
    @Getter
    private ContentCreator lastContentCreatorSelected;

    /**
     * Update admin.
     */
    public static void updateAdmin() {
        admin = Admin.getInstance();
    }

    /**
     * Instantiates a new Search bar.
     *
     * @param user the user
     */
    public SearchBar(final String user) {
        this.results = new ArrayList<>();
        this.resultsContentCreator = new ArrayList<>();
        this.user = user;
    }

    /**
     * Clear selection.
     */
    public void clearSelection() {
        lastSelected = null;
        lastSearchType = null;
    }

    /**
     * Search list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the list
     */
    public List<LibraryEntry> search(final Filters filters, final String type) {
        List<LibraryEntry> entries;

        switch (type) {
            case "song":
                entries = new ArrayList<>(admin.getSongs());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getAlbum() != null) {
                    entries = filterByAlbum(entries, filters.getAlbum());
                }

                if (filters.getTags() != null) {
                    entries = filterByTags(entries, filters.getTags());
                }

                if (filters.getLyrics() != null) {
                    entries = filterByLyrics(entries, filters.getLyrics());
                }

                if (filters.getGenre() != null) {
                    entries = filterByGenre(entries, filters.getGenre());
                }

                if (filters.getReleaseYear() != null) {
                    entries = filterByReleaseYear(entries, filters.getReleaseYear());
                }

                if (filters.getArtist() != null) {
                    entries = filterByArtist(entries, filters.getArtist());
                }

                break;
            case "playlist":
                entries = new ArrayList<>(admin.getPlaylists());

                entries = filterByPlaylistVisibility(entries, user);

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                if (filters.getFollowers() != null) {
                    entries = filterByFollowers(entries, filters.getFollowers());
                }

                break;
            case "podcast":
                entries = new ArrayList<>(admin.getPodcasts());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                break;
            case "album":
                entries = new ArrayList<>(admin.getAlbums());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                if (filters.getDescription() != null) {
                    entries = filterByDescription(entries, filters.getDescription());
                }

                break;
            default:
                entries = new ArrayList<>();
        }

        while (entries.size() > MAX_RESULTS) {
            entries.remove(entries.size() - 1);
        }

        this.results = entries;
        this.resultsContentCreator.clear();
        this.lastSearchType = type;
        return this.results;
    }

    /**
     * Search content creator list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the list
     */
    public List<ContentCreator> searchContentCreator(final Filters filters, final String type) {
        List<ContentCreator> entries;

        switch (type) {
            case "artist":
                entries = new ArrayList<>(admin.getArtists());

                if (filters.getName() != null) {
                    entries.removeIf(contentCreator
                                        -> !contentCreator.getUsername().toLowerCase()
                                                          .startsWith(filters.getName()
                                                          .toLowerCase()));
                }

                break;
            case "host":
                entries = new ArrayList<>(admin.getHosts());

                if (filters.getName() != null) {
                    entries.removeIf(contentCreator
                                        -> !contentCreator.getUsername().toLowerCase()
                                                          .startsWith(filters.getName()
                                                          .toLowerCase()));
                }

                break;
            default:
                entries = new ArrayList<>();
        }

        while (entries.size() > MAX_RESULTS) {
            entries.remove(entries.size() - 1);
        }

        this.resultsContentCreator = entries;
        this.results.clear();
        this.lastSearchType = type;
        return this.resultsContentCreator;
    }

    /**
     * Select library entry.
     *
     * @param itemNumber the item number
     * @return the library entry
     */
    public LibraryEntry select(final Integer itemNumber) {
        if (this.results.size() < itemNumber) {
            results.clear();

            return null;
        } else {
            lastSelected =  this.results.get(itemNumber - 1);
            results.clear();

            return lastSelected;
        }
    }

    /**
     * Select content creator content creator.
     *
     * @param itemNumber the item number
     * @return the content creator
     */
    public ContentCreator selectContentCreator(final Integer itemNumber) {
        if (this.resultsContentCreator.size() < itemNumber) {
            resultsContentCreator.clear();

            return null;
        } else {
            lastContentCreatorSelected = this.resultsContentCreator.get(itemNumber - 1);
            resultsContentCreator.clear();

            return lastContentCreatorSelected;
        }
    }
}
