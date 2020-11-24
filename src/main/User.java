package main;

import common.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;
    private int numberRatings = 0;
    private final Map<String, Double> ratingMovie = new HashMap<>();
    private final Map<String, ArrayList<Integer>> ratingSerial = new HashMap<>();


    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history,
                final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
    }

    public final String getUsername() {
        return username;
    }

    public final String getSubscriptionType() {
        return subscriptionType;
    }

    public final Map<String, Integer> getHistory() {
        return history;
    }

    public final ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public final int getNumberRatings() {
        return numberRatings;
    }

    public static Comparator<User> getcompByUser() {
        return new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Double.compare(u1.getNumberRatings(), u2.getNumberRatings());
            }
        };
    }

    /**
     * functia de video-uri favorite
     *
     * @param videoTitle
     * @return
     */
    public final String addFavorite(final String videoTitle) {
        if (videoTitle == null) {
            return null;
        } else {
            if (history.containsKey(videoTitle)) {
                if (!favoriteMovies.contains(videoTitle)) {
                    favoriteMovies.add(videoTitle);
                    return Constants.SUCCESS + videoTitle + Constants.FAVORITE_SUCCESS;
                } else {
                    return Constants.ERROR + videoTitle + Constants.FAVORITE_ERROR1;
                }
            } else {
                return Constants.ERROR + videoTitle + Constants.NOT_SEEN;
            }
        }
    }

    /**
     * functia de video-uri vazute
     *
     * @param videoTitle
     * @return
     */
    public final String addView(final String videoTitle) {
        if (videoTitle == null) {
            return null;
        } else {
            if (history.containsKey(videoTitle)) {
                history.replace(videoTitle, history.get(videoTitle) + 1);
                return Constants.SUCCESS + videoTitle + Constants.VIEW_SUCCESS
                        + history.get(videoTitle);
            } else {
                history.put(videoTitle, Constants.VIEW_CONST);
                return Constants.SUCCESS + videoTitle + Constants.VIEW_SUCCESS
                        + Constants.VIEW_CONST;
            }
        }
    }

    /**
     * @param movies
     * @param serials
     * @param videoTitle
     * @param rating
     * @param season
     * @return
     */
    public final String addRating(final ArrayList<Movie> movies, final ArrayList<Serial> serials,
                                  String videoTitle, final double rating, final int season) {
        if (videoTitle == null) {
            return null;
        } else {
            if (history.containsKey(videoTitle)) {
                for (Movie movie : movies) {
                    if (movie.getTitle().equals(videoTitle)) {
                        if (ratingMovie.get(movie.getTitle()) == null) {
                            ratingMovie.put(movie.getTitle(), movie.getRating());
                            movie.setRating(rating);
                            numberRatings++;
                            return Constants.SUCCESS + videoTitle + Constants.RATED
                                    + rating + Constants.BY + username;
                        } else {
                            return Constants.ERROR + videoTitle + Constants.AL_RATED;
                        }
                    }
                }
                for (Serial serial : serials) {
                    if (serial.getTitle().equals(videoTitle)) {
                        ratingSerial.computeIfAbsent(videoTitle, k -> new ArrayList<>());
                        if (ratingSerial.get(videoTitle).contains(season)) {
                            return Constants.ERROR + videoTitle + Constants.AL_RATED;
                        } else {
                            ArrayList<Integer> seasonBuff = ratingSerial.get(videoTitle);
                            seasonBuff.add(season);
                            ratingSerial.put(videoTitle, seasonBuff);
                            numberRatings++;
                            return Constants.SUCCESS + videoTitle + Constants.RATED + rating
                                    + Constants.BY + username;
                        }
                    }
                }
            } else {
                return Constants.ERROR + videoTitle + Constants.NOT_SEEN;
            }
        }
        return Constants.ERROR + videoTitle + Constants.NOT_SEEN;
    }

    /**
     * @param sortType
     * @param number
     * @param users
     * @return
     */
    public static String ratingsNumber(String sortType, final int number, final ArrayList<User> users) {
        ArrayList<User> usersBuff = new ArrayList<>();
        if (sortType.equals(Constants.ASC)) {
            users.sort(getcompByUser());
        } else {
            users.sort(getcompByUser().reversed());
        }
        for (int i = 0; i < number; i++) {
            if (i == users.size()) {
                break;
            }
            if (users.get(i).getNumberRatings() == 0) {
                continue;
            }
            usersBuff.add(users.get(i));
        }
        return Constants.QUERY_RES + usersBuff.toString();
    }

    @Override
    public String toString() {
        return username;
    }
}
