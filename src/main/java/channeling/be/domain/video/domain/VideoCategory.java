package channeling.be.domain.video.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum VideoCategory {
    NONE("0", "None"),
    FILM_AND_ANIMATION("1", "Film & Animation"),
    AUTOS_AND_VEHICLES("2", "Autos & Vehicles"),
    MUSIC("10", "Music"),
    PETS_AND_ANIMALS("15", "Pets & Animals"),
    SPORTS("17", "Sports"),
    SHORT_MOVIES("18", "Short Movies"),
    TRAVEL_AND_EVENTS("19", "Travel & Events"),
    GAMING("20", "Gaming"),
    VIDEOBLOGGING("21", "Videoblogging"),
    PEOPLE_AND_BLOGS("22", "People & Blogs"),
    COMEDY("23", "Comedy"),
    ENTERTAINMENT("24", "Entertainment"),
    NEWS_AND_POLITICS("25", "News & Politics"),
    HOWTO_AND_STYLE("26", "Howto & Style"),
    EDUCATION("27", "Education"),
    SCIENCE_AND_TECHNOLOGY("28", "Science & Technology"),
    MOVIES("30", "Movies"),
    ANIME_ANIMATION("31", "Anime/Animation"),
    ACTION_ADVENTURE("32", "Action/Adventure"),
    CLASSICS("33", "Classics"),
    COMEDY_GENRE("34", "Comedy"),
    DOCUMENTARY("35", "Documentary"),
    DRAMA("36", "Drama"),
    FAMILY("37", "Family"),
    FOREIGN("38", "Foreign"),
    HORROR("39", "Horror"),
    SCI_FI_FANTASY("40", "Sci-Fi/Fantasy"),
    THRILLER("41", "Thriller"),
    SHORTS("42", "Shorts"),
    SHOWS("43", "Shows"),
    TRAILERS("44", "Trailers");

    private final String id;
    private final String title;

    public static VideoCategory ofId(String id) {
        return Arrays.stream(VideoCategory.values())
            .filter(category -> category.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid VideoCategory id: " + id));
    }
}
