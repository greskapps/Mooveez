package com.greskapps.android.mooveez;

import com.greskapps.android.mooveez.BuildConfig;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetUtils {
    final static String MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    final static String API_KEY = BuildConfig.API_KEY;

    public static URL urlBuilder(String sortPreferences) {
        Uri builtUri = Uri.parse(MOVIE_URL + sortPreferences)
                .buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("page", "1")
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL urlBuilderTrailers(String videoId) {
        Uri builtUri = Uri.parse(MOVIE_URL + videoId + "/videos")
                .buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US")
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL urlBuilderReviews(String videoId) {
        Uri builtUri = Uri.parse(MOVIE_URL + videoId + "/reviews")
                .buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("page", "1")
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getMovieList(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream streamMovie = connection.getInputStream();
            Scanner scanner = new Scanner(streamMovie);
            scanner.useDelimiter("\\A");
            StringBuilder lastString = new StringBuilder();

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                do {
                    String toAdd = scanner.next();
                    lastString.append(toAdd);
                    hasInput = scanner.hasNext();
                } while (hasInput);
                return lastString.toString();
            } else {
                return null;
            }

        } finally {
            connection.disconnect();
        }

    }

}
