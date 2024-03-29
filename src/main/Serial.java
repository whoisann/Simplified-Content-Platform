package main;

import common.Constants;
import entertainment.Season;
import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Collections;

public class Serial extends Show {

    private final int numberOfSeasons;
    private final ArrayList<Season> seasons;
    private double rating;

    public Serial(final String title, final int year, final ArrayList<String> genres,
                  final ArrayList<String> cast, final int numberOfSeasons,
                  final ArrayList<Season> seasons) {

        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
        this.rating = rating;
    }

    public final ArrayList<Season> getSeasons() {
        return seasons;
    }

    public final void setRating(final double rating) {
        this.rating = rating;
    }

    /**
     * Calculate season duration
     */
    public int getDuration() {
        int duration = 0;
        for (Season season : seasons) {
            duration = duration + season.getDuration();
        }
        return duration;
    }

    /**
     * Calculate serial rating
     */
    public double getSerialRating() {
        double ratingTotal = 0.0;
        for (Season season : seasons) {
            rating = 0.0;
            for (int i = 0; i < season.getRatings().size(); i++) {
                rating = rating + season.getRatings().get(i);
            }
            if (season.getRatings().size() != 0) {
                rating = rating / season.getRatings().size();
            }
            ratingTotal = ratingTotal + rating;
        }
        ratingTotal = ratingTotal / numberOfSeasons;
        return ratingTotal;
    }

    /**
     * Compare serials first by an integer (views/nr of favorites) and second by name
     */
    public static Comparator<Serial> getCompByInt(final HashMap<String, Integer> compSerial) {
        return new Comparator<Serial>() {
            @Override
            public int compare(final Serial s1, final Serial s2) {
                if (Integer.compare(compSerial.get(s1.getTitle()),
                        compSerial.get(s2.getTitle())) == 0) {
                    return s1.getTitle().compareTo(s2.getTitle());
                }
                return Integer.compare(compSerial.get(s1.getTitle()),
                        compSerial.get(s2.getTitle()));
            }
        };
    }

    /**
     * Compare serials first by a double (ratings) and second by name
     */
    public static Comparator<Serial> getCompByDouble(final HashMap<String, Double> compSerial) {
        return new Comparator<Serial>() {
            @Override
            public int compare(final Serial s1, final Serial s2) {
                if (Double.compare(compSerial.get(s1.getTitle()),
                        compSerial.get(s2.getTitle())) == 0) {
                    return s1.getTitle().compareTo(s2.getTitle());
                }
                return Double.compare(compSerial.get(s1.getTitle()),
                        compSerial.get(s2.getTitle()));
            }
        };
    }

    /**
     * Function to determine first N serials sorted by views
     */
    public static String mostViewed(final ActionInputData currentCommand,
                                    final ArrayList<Serial> serials,
                                    final ArrayList<User> users) {

        // hashmap that contains serials' name and number of views
        HashMap<String, Integer> viewsSerial = new HashMap<>();
        ArrayList<Serial> serialsBuff = new ArrayList<>(serials);
        ArrayList<String> serialsList = new ArrayList<>();

        for (Serial serial : serials) {
            viewsSerial.put(serial.getTitle(), 0);
        }

        // populate the hashmap
        for (User user : users) {
            for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
                if (viewsSerial.containsKey(entry.getKey())) {
                    viewsSerial.put(entry.getKey(), entry.getValue()
                            + viewsSerial.get(entry.getKey()));
                } else {
                    viewsSerial.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // filter serials by input filters year and genre
        for (int i = serialsBuff.size() - 1; i >= 0; i--) {
            if (currentCommand.getFilters().get(0).get(0) != null) {
                if (!Integer.toString(serialsBuff.get(i).getYear()).
                        equals(currentCommand.getFilters().get(0).get(0))) {
                    serialsBuff.remove(i);
                    continue;
                }
            }
            if (currentCommand.getFilters().get(1).get(0) != null) {
                if (!serialsBuff.get(i).getGenres().
                        contains(currentCommand.getFilters().get(1).get(0))) {
                    serialsBuff.remove(i);
                }
            }
        }

        // sort the hashmap
        serialsBuff.sort(getCompByInt(viewsSerial));
        if (currentCommand.getSortType().equals(Constants.DSC)) {
            Collections.reverse(serialsBuff);
        }

        // selecting first N movies
        for (int i = 0, j = 0; i < serialsBuff.size() && j < currentCommand.getNumber(); i++) {
            if (viewsSerial.get(serialsBuff.get(i).getTitle()) != 0) {
                j++;
                serialsList.add(serialsBuff.get(i).getTitle());
            }
        }
        return Constants.QUERY_RES + serialsList.toString();
    }

    /**
     * Function to sort first N serials by the number of appearances in users'
     * favorite video lists
     */
    public static String favoriteSerials(final ActionInputData currentCommand,
                                         final ArrayList<Serial> serials,
                                         final ArrayList<User> users) {

        // hashmap that contains serials' name and number of views
        HashMap<String, Integer> favoriteSerials = new HashMap<>();
        ArrayList<Serial> serialsBuff = new ArrayList<>(serials);
        ArrayList<String> serialList = new ArrayList<>();

        for (Serial serial : serials) {
            favoriteSerials.put(serial.getTitle(), 0);
        }

        // populate the hashmap
        for (User user : users) {
            for (String videoTitle : user.getFavoriteShows()) {
                for (Serial serial : serials) {
                    if (serial.getTitle().equals(videoTitle)) {
                        if (favoriteSerials.containsKey(videoTitle)) {
                            favoriteSerials.put(videoTitle, favoriteSerials.get(videoTitle) + 1);
                        } else {
                            favoriteSerials.put(videoTitle, 1);
                        }
                    }
                }
            }
        }

        // filter serials by input filters year and genre
        for (int i = serialsBuff.size() - 1; i >= 0; i--) {
            if (currentCommand.getFilters().get(0).get(0) != null) {
                if (!Integer.toString(serialsBuff.get(i).getYear()).
                        equals(currentCommand.getFilters().get(0).get(0))) {
                    serialsBuff.remove(i);
                    continue;
                }
            }
            if (currentCommand.getFilters().get(1).get(0) != null) {
                if (!serialsBuff.get(i).getGenres().
                        contains(currentCommand.getFilters().get(1).get(0))) {
                    serialsBuff.remove(i);
                }
            }
        }

        // sort the serials
        serialsBuff.sort(getCompByInt(favoriteSerials));
        if (currentCommand.getSortType().equals(Constants.DSC)) {
            Collections.reverse(serialsBuff);
        }

        // selecting first N movies
        for (int i = 0, j = 0; i < serialsBuff.size() && j < currentCommand.getNumber(); i++) {
            if (favoriteSerials.containsKey(serialsBuff.get(i).getTitle())) {
                if (favoriteSerials.get(serialsBuff.get(i).getTitle()) != 0) {
                    j++;
                    serialList.add(serialsBuff.get(i).getTitle());
                }
            }
        }
        return Constants.QUERY_RES + serialList.toString();
    }

    /**
     * Function to determine first N serials sorted by their length
     */
    public static String longestSerial(final ActionInputData currentCommand,
                                       final ArrayList<Serial> serials) {

        // hashmap containing serials' name and length
        HashMap<String, Integer> longMovie = new HashMap<>();
        ArrayList<Serial> serialBuff = new ArrayList<>(serials);
        ArrayList<String> moviesList = new ArrayList<>();

        for (Serial serial : serialBuff) {
            longMovie.put(serial.getTitle(), serial.getDuration());
        }

        // populate the hashmap
        for (int i = serialBuff.size() - 1; i >= 0; i--) {
            if (currentCommand.getFilters().get(0).get(0) != null) {
                if (!Integer.toString(serialBuff.get(i).getYear()).
                        equals(currentCommand.getFilters().get(0).get(0))) {
                    serialBuff.remove(i);
                    continue;
                }
            }
            if (currentCommand.getFilters().get(1).get(0) != null) {
                if (!serialBuff.get(i).getGenres().
                        contains(currentCommand.getFilters().get(1).get(0))) {
                    serialBuff.remove(i);
                }
            }
        }

        // sort the serials
        serialBuff.sort(getCompByInt(longMovie));
        if (currentCommand.getSortType().equals(Constants.DSC)) {
            Collections.reverse(serialBuff);
        }

        // selecting first N movies
        for (int i = 0, j = 0; i < serialBuff.size()
                && j < currentCommand.getNumber(); i++) {
            if (longMovie.get(serialBuff.get(i).getTitle()) != 0) {
                j++;
                moviesList.add(serialBuff.get(i).getTitle());
            }
        }
        return Constants.QUERY_RES + moviesList.toString();
    }

    /**
     * Function to sort first N serials after rating
     */
    public static String ratingSerial(final ActionInputData currentCommand,
                                      final ArrayList<Serial> serials) {

        // hashmap that contains serials' name and rating
        HashMap<String, Double> ratingSerial = new HashMap<>();
        ArrayList<Serial> serialsBuff = new ArrayList<>(serials);
        ArrayList<String> serialsList = new ArrayList<>();

        // populate the hashmap
        for (Serial serial : serialsBuff) {
            ratingSerial.put(serial.getTitle(), serial.getSerialRating());
        }

        // filter serials by input filters year and genre
        for (int i = serialsBuff.size() - 1; i >= 0; i--) {
            if (currentCommand.getFilters().get(0).get(0) != null) {
                if (!Integer.toString(serialsBuff.get(i).getYear()).
                        equals(currentCommand.getFilters().get(0).get(0))) {
                    serialsBuff.remove(i);
                    continue;
                }
            }
            if (currentCommand.getFilters().get(1).get(0) != null) {
                if (!serialsBuff.get(i).getGenres().
                        contains(currentCommand.getFilters().get(1).get(0))) {
                    serialsBuff.remove(i);
                }
            }
        }

        // sort the hashmap
        serialsBuff.sort(getCompByDouble(ratingSerial));
        if (currentCommand.getSortType().equals(Constants.DSC)) {
            Collections.reverse(serialsBuff);
        }

        // selecting first N serials
        for (int i = 0, j = 0; i < serialsBuff.size()
                && j < currentCommand.getNumber(); i++) {
            if (ratingSerial.get(serialsBuff.get(i).getTitle()) != 0) {
                j++;
                serialsList.add(serialsBuff.get(i).getTitle());
            }
        }
        return Constants.QUERY_RES + serialsList.toString();
    }

}
