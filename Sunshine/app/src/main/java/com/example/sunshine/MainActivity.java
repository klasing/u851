package com.example.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.utilities.FakeDataUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ForecastAdapter.ForecastAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    private static final int ID_FORECAST_LOADER = 44;

    private ForecastAdapter mForecastAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);

        FakeDataUtils.insertFakeData(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this, this);

        mRecyclerView.setAdapter(mForecastAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
    }

    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {

           case ID_FORECAST_LOADER:

                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showWeatherDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mForecastAdapter.swapCursor(null);
    }

//    @Override
//    public void onClick(long date) {
//        Context context = this;
//        Class destinationClass = DetailActivity.class;
//        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
//        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
//        startActivity(intentToStartDetailActivity);
//    }

    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }

    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
//package com.example.sunshine;
///*
// * File -> Settings
// * Editor -> File and Code templates
// * select Class from list
// * enter following text in right window
// * #if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end #parse("File Header.java") public class ${NAME} { }
// */
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.LoaderManager.LoaderCallbacks;
//import android.support.v4.content.AsyncTaskLoader;
//import android.support.v4.content.Loader;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.preference.PreferenceManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.example.sunshine.data.SunshinePreferences;
//import com.example.sunshine.utilities.NetworkUtils;
//import com.example.sunshine.utilities.OpenWeatherJsonUtils;
//
//import java.net.URL;
//
//public class MainActivity extends AppCompatActivity implements
//        ForecastAdapter.ForecastAdapterOnClickHandler,
//        LoaderCallbacks<String[]>,
//        SharedPreferences.OnSharedPreferenceChangeListener {
//
//    private static final String TAG = MainActivity.class.getSimpleName();
//
//    private RecyclerView mRecyclerView;
//    private ForecastAdapter mForecastAdapter;
//
//    private TextView mErrorMessageDisplay;
//
//    private ProgressBar mLoadingIndicator;
//
//    private static final int FORECAST_LOADER_ID = 0;
//
//    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_forecast);
//
//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
//
//        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
//
//        int recyclerViewOrientation = LinearLayoutManager.VERTICAL;
//        boolean shouldReverseLayout = false;
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, recyclerViewOrientation, shouldReverseLayout);
//
//        mRecyclerView.setLayoutManager(layoutManager);
//
//        mRecyclerView.setHasFixedSize(true);
//
//        mForecastAdapter = new ForecastAdapter(this);
//
//        mRecyclerView.setAdapter(mForecastAdapter);
//
//        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
//
//        int loaderId = FORECAST_LOADER_ID;
//
//        LoaderCallbacks<String[]> callback = MainActivity.this;
//
//        Bundle bundleForLoader = null;
//
//        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
//
//        Log.d(TAG, "onCreate: registering preference changed listener");
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .registerOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {
//
//        return new AsyncTaskLoader<String[]>(this) {
//
//            String[] mWeatherData = null;
//
//            @Override
//            protected void onStartLoading() {
//                if (mWeatherData != null) {
//                    deliverResult(mWeatherData);
//                } else {
//                    mLoadingIndicator.setVisibility(View.VISIBLE);
//                    forceLoad();
//                }
//            }
//
//            @Override
//            public String[] loadInBackground() {
//
//                String locationQuery = SunshinePreferences
//                        .getPreferredWeatherLocation(MainActivity.this);
//
//                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);
//
//                try {
//                    String jsonWeatherResponse = NetworkUtils
//                            .getResponseFromHttpUrl(weatherRequestUrl);
//
//                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
//                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
//
//                    return simpleJsonWeatherData;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//
//            public void deliverResult(String[] data) {
//                mWeatherData = data;
//                super.deliverResult(data);
//            }
//        };
//    }
//
//    @Override
//    public void onLoadFinished(Loader<String[]> loader, String[] data) {
//        mLoadingIndicator.setVisibility(View.INVISIBLE);
//        mForecastAdapter.setWeatherData(data);
//        if (null == data) {
//            showErrorMessage();
//        } else {
//            showWeatherDataView();
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<String[]> loader) {
//    }
//
//    private void invalidateData() {
//
//        mForecastAdapter.setWeatherData(null);
//    }
//
//    private void openLocationInMap() {
//        //String addressString = "1600 Ampitheatre Parkway, CA";
//        String addressString = SunshinePreferences.getPreferredWeatherLocation(this);
//        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(geoLocation);
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Log.d(TAG, "Couldn't call " + geoLocation.toString()
//                    + ", no receiving apps installed!");
//        }
//    }
//
//    @Override
//    public void onClick(String weatherForDay) {
//        Context context = this;
//        Class destinationClass = DetailActivity.class;
//        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
//        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
//        startActivity(intentToStartDetailActivity);
//    }
//
//    private void showWeatherDataView() {
//        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
//
//        mRecyclerView.setVisibility(View.VISIBLE);
//    }
//
//    private void showErrorMessage() {
//        mRecyclerView.setVisibility(View.INVISIBLE);
//
//        mErrorMessageDisplay.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (PREFERENCES_HAVE_BEEN_UPDATED) {
//            Log.d(TAG, "onStart: preferences were updated");
//            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
//            PREFERENCES_HAVE_BEEN_UPDATED = false;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .unregisterOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.forecast, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_refresh) {
//            invalidateData();
//            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
//            return true;
//        }
//
//        if (id == R.id.action_map) {
//            openLocationInMap();
//            return true;
//        }
//
//        if (id == R.id.action_settings) {
//            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
//            startActivity(startSettingsActivity);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        PREFERENCES_HAVE_BEEN_UPDATED = true;
//    }
//}
