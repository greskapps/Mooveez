package com.greskapps.android.mooveez;

import com.greskapps.android.mooveez.ThumbnailAdapter;
import com.greskapps.android.mooveez.NetUtils;
import com.greskapps.android.mooveez.DatabaseIntoJSON;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, ThumbnailAdapter.movieClickListener {

    private static int LOADER_MOVIE_ID = 2018;

    private static String SORT_BY_POP = "popular";
    private static String SORT_BY_RATINGS = "top_rated";
    private static String SORT_FAVORITES = "favorites";
    private static String currentSort;

    private RecyclerView mainRecView;
    private RecyclerView.Adapter adapter;
    private List<Thumbnail> thumbnailList;
    private Parcelable loadedState;

    private ProgressBar progress;
    private TextView noInternet;
    private Boolean optionsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progress);
        noInternet = (TextView) findViewById(R.id.noInternet);
        optionsSelected = false;

        Bundle sortType = new Bundle();
        sortType.putString("TYPE_SORT", SORT_BY_POP);

        //load the initial URL only if the rv is empty
        if (mainRecView == null) {
            thumbnailList = new ArrayList<>();

            if (currentSort != null) {
                initiateLoading(currentSort);
            } else {
                initiateLoading(SORT_BY_POP);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("rvState", mainRecView.getLayoutManager().onSaveInstanceState());
        outState.putString("CATEGORY_LOAD", currentSort);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        loadedState = savedInstanceState.getParcelable("rvState");
        currentSort = savedInstanceState.getString("CATEGORY_LOAD");
    }

    @Override
    protected void onPause() {
        super.onPause();

        loadedState = mainRecView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            mainRecView.getLayoutManager().onRestoreInstanceState(loadedState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiateLoading(String sort) {
        currentSort = sort;

        if (sort != SORT_FAVORITES) {
            Context context = getApplicationContext();

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected == true) {
                noInternet.setVisibility(View.INVISIBLE);
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<String> movieLoader = loaderManager.getLoader(LOADER_MOVIE_ID);
                Bundle sortType = new Bundle();
                sortType.putString("TYPE_SORT", sort);

                if (movieLoader == null) {
                    loaderManager.initLoader(LOADER_MOVIE_ID, sortType, this);
                } else {
                    loaderManager.restartLoader(LOADER_MOVIE_ID, sortType, this);
                }
            } else {
                //we show the no connection
                noInternet.setText(R.string.no_internet);
                noInternet.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
            }
        } else {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> movieLoader = loaderManager.getLoader(LOADER_MOVIE_ID);
            Bundle sortType = new Bundle();
            sortType.putString("TYPE_SORT", sort);
            if (movieLoader == null) {
                loaderManager.initLoader(LOADER_MOVIE_ID, sortType, this);
            } else {
                loaderManager.restartLoader(LOADER_MOVIE_ID, sortType, this);
            }
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                progress.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                try {
                    Bundle arg = args;
                    String sorting = args.getString("TYPE_SORT");

                    if (sorting != SORT_FAVORITES) {
                        //for the web loaders
                        String lastString = NetUtils.getMovieList(NetUtils.urlBuilder(sorting));
                        return lastString;
                    } else {
                        //for the content provider loader
                        String lastString = DatabaseIntoJSON.FullDatabaseToJson(getContext());
                        return lastString;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        try {
            if (data != null) {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                thumbnailList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Thumbnail thumbs = new Thumbnail(object.getString("original_title")
                            , object.getString("poster_path")
                            , object.getString("backdrop_path")
                            , object.getString("overview")
                            , object.getString("release_date")
                            , object.getInt("vote_average")
                            , object.getString("id"));
                    thumbnailList.add(thumbs);
                }

                if (jsonArray.length() == 0) {
                    noInternet.setText(R.string.no_favorites);
                    noInternet.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    noInternet.setVisibility(View.INVISIBLE);
                }

// possible fix for erroneous code?
// http://www.chansek.com/RecyclerView-no-adapter-attached-skipping-layout/
                mainRecView = findViewById(R.id.thumbs_rv);
                GridLayoutManager manager = new GridLayoutManager(this, 2);
                mainRecView.setLayoutManager(manager);
                mainRecView.setHasFixedSize(true);
                adapter = new ThumbnailAdapter(thumbnailList, getApplicationContext(), this);
                mainRecView.setAdapter(adapter);

//  original code producing error?
//                mainRecView = (RecyclerView) findViewById(R.id.thumbs_rv);
//                mainRecView.setLayoutManager(new GridLayoutManager(this, 2));
//                adapter = new ThumbnailAdapter(thumbnailList, getApplicationContext(), this);
//                mainRecView.setAdapter(adapter);
                if (optionsSelected == false) {
                    mainRecView.getLayoutManager().onRestoreInstanceState(loadedState);
                } else {
                    optionsSelected = false;
                }
            } else {
                progress.setVisibility(View.INVISIBLE);
                noInternet.setVisibility(View.VISIBLE);
                noInternet.setText(R.string.no_internet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onMovieItemClick(Thumbnail contentClicked) {
        //start new activity
        Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
        intent.putExtra("detailParce", contentClicked);
        startActivity(intent);
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //we reset the position
        mainRecView.getLayoutManager().scrollToPosition(0);
        optionsSelected = true;

        switch (item.getItemId()) {
            case R.id.menu_pop:
                initiateLoading(SORT_BY_POP);
                return true;
            case R.id.menu_top:
                initiateLoading(SORT_BY_RATINGS);
                return true;
            case R.id.menu_fav:
                initiateLoading(SORT_FAVORITES);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}