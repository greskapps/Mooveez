package com.greskapps.android.mooveez;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.greskapps.android.mooveez.ReviewsAdapter;
import com.greskapps.android.mooveez.Reviews;
import com.greskapps.android.mooveez.Thumbnail;
import com.greskapps.android.mooveez.Trailers;
import com.greskapps.android.mooveez.TrailersAdapter;
import com.greskapps.android.mooveez.NetUtils;
// import com.greskapps.android.mooveez.favoritesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>>, TrailersAdapter.movieClickListener, ReviewsAdapter.movieClickListener {

    private static int LOADER_TRAILER_ID = 1977;
    private static int[] loadedState = new int[]{0, 0};
    private static Parcelable loadedStateTrailer;
    private static Parcelable loadedStateReviews;
    private RecyclerView trailerRecView;
    private RecyclerView.Adapter trailersAdapter;
    private RecyclerView reviewRecView;
    private RecyclerView.Adapter reviewsAdapter;
    private List<Trailers> trailerList;
    private List<Reviews> reviewList;
    private Thumbnail object;
    private TextView title_tv;
    private TextView release_tv;
    private TextView plot_tv;
    private ImageView poster_iv;
    private ImageView thumb_iv;
    private RatingBar ratings_bar;
    private ImageView star_favorite;
    private ScrollView details_sv;
    private Boolean mIsFavorite;

    private String movieID;

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        details_sv = findViewById(R.id.details_sv);

        title_tv = findViewById(R.id.title_tv);
        release_tv = findViewById(R.id.release_tv);
        plot_tv = findViewById(R.id.plot_tv);
        poster_iv = findViewById(R.id.poster_iv);
        thumb_iv = findViewById(R.id.thumb_iv);
        ratings_bar = findViewById(R.id.ratings_bar);
//        star_favorite = findViewById(R.id.star_favorite);
        progress = findViewById(R.id.details_progress);

/*        star_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFavorite != null) {
                    if (mIsFavorite) {
                        deleteFavorite();
                    } else {
                        saveFavorite();
                    }
                }
            }
        });*/


        object = (Thumbnail) getIntent().getParcelableExtra("detailParce");
        title_tv.setText(object.getTitle());
        release_tv.setText(object.getRelease_date());
        plot_tv.setText(object.getDetails());
        ratings_bar.setRating(object.getVote_average());
        movieID = object.getmovieId();

        if (trailerRecView == null) {
            progress.setVisibility(View.VISIBLE);

            Picasso.get().load("https://image.tmdb.org/t/p/w500" + object.getUrl()).into(thumb_iv);
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + object.getwideUrl()).into(poster_iv);

            trailerList = new ArrayList<>();
            reviewList = new ArrayList<>();

            loadTrailers(movieID, "trailer");

//            checkFavorite();
        }
    }

    // scroll saving
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("scrollX", details_sv.getScrollX());
        outState.putInt("scrollY", details_sv.getScrollY());
        if (trailerRecView != null) {
            outState.putParcelable("savedTrailer", trailerRecView.getLayoutManager().onSaveInstanceState());
        }
        if (reviewRecView != null) {
            outState.putParcelable("savedReview", reviewRecView.getLayoutManager().onSaveInstanceState());
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        loadedState[0] = savedInstanceState.getInt("scrollX");
        loadedState[1] = savedInstanceState.getInt("scrollY");

        loadedStateTrailer = savedInstanceState.getParcelable("savedTrailer");
        loadedStateReviews = savedInstanceState.getParcelable("savedReview");
    }

    @Override
    protected void onPause() {
        super.onPause();

        loadedState[0] = details_sv.getScrollX();
        loadedState[1] = details_sv.getScrollY();

        if (trailerRecView != null) {
            loadedStateTrailer = trailerRecView.getLayoutManager().onSaveInstanceState();
        }
        if (reviewRecView != null) {
            loadedStateReviews = reviewRecView.getLayoutManager().onSaveInstanceState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (loadedState != null) {
            details_sv.post(new Runnable() {
                @Override
                public void run() {
                    details_sv.scrollTo(loadedState[0], loadedState[1]);
                }
            });
        }
    }

    private void loadTrailers(String movieID, String typeOfLoad) {
        Context context = getApplicationContext();

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> movieLoader = loaderManager.getLoader(LOADER_TRAILER_ID);
            Bundle sortType = new Bundle();
            sortType.putString("TYPE_LOAD", typeOfLoad);

            if (movieLoader == null) {
                loaderManager.initLoader(LOADER_TRAILER_ID, sortType, this);
            } else {
                loaderManager.restartLoader(LOADER_TRAILER_ID, sortType, this);
            }
        } else {
        }
    }


    @Override
    public Loader<List<String>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<String>>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<String> loadInBackground() {
                try {
                    Bundle arg = args;
                    String type_load = args.getString("TYPE_LOAD");
                    List<String> toReturn = new ArrayList<>();
                    ;
                    if (type_load == "trailer") {
                        String lastString = NetUtils.getMovieList(NetUtils.urlBuilderTrailers(movieID));
                        toReturn.add(type_load);
                        toReturn.add(lastString);
                        return toReturn;
                    } else {
                        String lastString = NetUtils.getMovieList(NetUtils.urlBuilderReviews(movieID));
                        toReturn.add(type_load);
                        toReturn.add(lastString);
                        return toReturn;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        try {
            if (data != null && data.get(0) != null && data.get(1) != null) {
                if (data.get(0) == "trailer") {
                    JSONObject jsonObject = new JSONObject(data.get(1));
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    trailerList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        Trailers trailerThumbs = new Trailers(object.getString("key")
                                , object.getString("name")
                                , object.getString("type"));
                        trailerList.add(trailerThumbs);
                    }

// possible fix for erroneous code?
// http://www.chansek.com/RecyclerView-no-adapter-attached-skipping-layout/
                    trailerRecView = findViewById(R.id.trailers_rv);
                    LinearLayoutManager manager = new LinearLayoutManager(this);
                    trailerRecView.setLayoutManager(manager);
                    trailerRecView.setHasFixedSize(true);
                    trailersAdapter = new TrailersAdapter(trailerList, getApplicationContext(), this);
                    trailerRecView.setAdapter(trailersAdapter);
                    trailersAdapter.notifyDataSetChanged();

//  original code producing error?
//                    trailerRecView = (RecyclerView) findViewById(R.id.trailers_rv);
//                    trailerRecView.setLayoutManager(new LinearLayoutManager(this));
//                    trailersAdapter = new TrailersAdapter(trailerList, getApplicationContext(), this);
//                    trailerRecView.setAdapter(trailersAdapter);
//                    trailersAdapter.notifyDataSetChanged();

                    loadTrailers(movieID, "reviews");

                } else {

                    JSONObject jsonObject = new JSONObject(data.get(1));
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    reviewList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        Reviews reviewThumbs = new Reviews(object.getString("author"),
                                object.getString("content"),
                                object.getString("url"));
                        reviewList.add(reviewThumbs);
                    }

// possible fix for erroneous code?
// http://www.chansek.com/RecyclerView-no-adapter-attached-skipping-layout/
                    reviewRecView = findViewById(R.id.reviews_rv);
                    LinearLayoutManager manager = new LinearLayoutManager(this);
                    reviewRecView.setLayoutManager(manager);
                    reviewRecView.setHasFixedSize(true);
                    reviewsAdapter = new ReviewsAdapter(reviewList, getApplicationContext(), this);
                    reviewRecView.setAdapter(reviewsAdapter);
                    reviewsAdapter.notifyDataSetChanged();

//  original code producing error?
//                    reviewRecView = (RecyclerView) findViewById(R.id.reviews_rv);
//                    reviewRecView.setLayoutManager(new LinearLayoutManager(this));
//                    reviewsAdapter = new ReviewsAdapter(reviewList, getApplicationContext(), this);
//                    reviewRecView.setAdapter(reviewsAdapter);
//                    reviewsAdapter.notifyDataSetChanged();

                    if (loadedState != null) {
                        details_sv.post(new Runnable() {
                            @Override
                            public void run() {
                                details_sv.scrollTo(loadedState[0], loadedState[1]);
                            }
                        });
                    }

                    if (loadedStateTrailer != null) {
                        trailerRecView.getLayoutManager().onRestoreInstanceState(loadedStateTrailer);
                    }

                    if (loadedStateReviews != null) {
                        reviewRecView.getLayoutManager().onRestoreInstanceState(loadedStateReviews);
                    }

                    progress.setVisibility(View.INVISIBLE);
                }

            } else {

                progress.setVisibility(View.INVISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }

    @Override
    public void onMovieItemClick(Trailers clicked) {
        //we play the video
        String youtubeURL = "https://www.youtube.com/watch?v=" + clicked.getMovieID();

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + clicked.getMovieID()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + clicked.getMovieID()));

        Context context = getApplicationContext();
        try {
            context.startActivity(appIntent);

        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public void onMovieItemClick(Reviews clicked) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clicked.getUrl()));

        Context context = getApplicationContext();
        context.startActivity(webIntent);
    }


    private void deleteFavorite() {
        int rowDeleted = 0;

        String selection;
        String[] selectionArgs = {""};

        selection = favContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?";
        selectionArgs[0] = movieID;

        rowDeleted = getContentResolver().delete(favContract.FavoritesEntry.FAVORITES_URI,
                selection,
                selectionArgs);

        checkFavorite();

    }

    private void saveFavorite() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(favContract.FavoritesEntry.COLUMN_MOVIE_ID, object.getmovieId());
        contentValues.put(favContract.FavoritesEntry.COLUMN_TITLE, object.getTitle());
        contentValues.put(favContract.FavoritesEntry.COLUMN_DESCRIPTION, object.getDetails());
        contentValues.put(favContract.FavoritesEntry.COLUMN_URL, object.getUrl());
        contentValues.put(favContract.FavoritesEntry.COLUMN_WIDE_URL, object.getwideUrl());
        contentValues.put(favContract.FavoritesEntry.COLUMN_RELEASE_DATE, object.getRelease_date());
        contentValues.put(favContract.FavoritesEntry.COLUMN_VOTE_AVERAGE, object.getVote_average());

        Uri uri = getContentResolver().insert(favContract.BASE_URI.buildUpon().appendPath(favContract.PATH_FAVORITES).build(), contentValues);

        if (uri != null) {
            mIsFavorite = true;
            changeGraphicFavorites();
        }
    }

    private void changeGraphicFavorites() {
        if (mIsFavorite) {
            star_favorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            star_favorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void checkFavorite() {
        String selection;
        String[] selectionArgs = {""};

        selection = favContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?";
        selectionArgs[0] = movieID;

        Cursor cursor = getApplicationContext().getContentResolver().query(favContract.FavoritesEntry.FAVORITES_URI,
                null,
                selection,
                selectionArgs,
                null,
                null
        );

        if (cursor.moveToFirst() == true) {
            mIsFavorite = true;
        } else {
            mIsFavorite = false;
        }

        changeGraphicFavorites();
    }


}
