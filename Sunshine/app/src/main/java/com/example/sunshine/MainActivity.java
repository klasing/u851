package com.example.sunshine;
/*
 * File -> Settings
 * Editor -> File and Code templates
 * select Class from list
 * enter following text in right window
 * #if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end #parse("File Header.java") public class ${NAME} { }
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
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
import android.widget.TextView;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

//public class MainActivity extends AppCompatActivity implements
//        ForecastAdapterOnClickHandler {
public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);

        mRecyclerView.setAdapter(mForecastAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        int loaderId = FORECAST_LOADER_ID;

        LoaderCallbacks<String[]> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        //loadWeatherData();
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {

                String locationQuery = SunshinePreferences
                        .getPreferredWeatherLocation(MainActivity.this);

                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                    return simpleJsonWeatherData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
    }

    private void invalidateData() {

        mForecastAdapter.setWeatherData(null);
    }

    private void openLocationInMap() {
        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }
    }

    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        //Toast.makeText(context, weatherForDay, Toast.LENGTH_SHORT)
        //        .show();
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(intentToStartDetailActivity);
    }

    private void showWeatherDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);

        mErrorMessageDisplay.setVisibility(View.VISIBLE);
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

        if (id == R.id.action_refresh) {
            //mWeatherTextView.setText("");
//            mForecastAdapter.setWeatherData(null);
//            loadWeatherData();
            invalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    private void loadWeatherData() {
//        showWeatherDataView();
//
//        String location = SunshinePreferences.getPreferredWeatherLocation(this);
//        new FetchWeatherTask().execute(location);
//    }




//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            if (params.length == 0) {
//                return null;
//            }
//
//            String location = params[0];
//            URL weatherRequestUrl = NetworkUtils.buildUrl(location);
//
//            try {
//                String jsonWeatherResponse = NetworkUtils
//                        .getResponseFromHttpUrl(weatherRequestUrl);
//
//                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
//                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
//
//                return simpleJsonWeatherData;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String[] weatherData) {
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (weatherData != null) {
//                showWeatherDataView();
//                mForecastAdapter.setWeatherData(weatherData);
//            } else {
//                showErrorMessage();
//            }
//        }
//    }

//    private void openLocationInMap() {
//        String addressString = "1600 Ampitheatre Parkway, CA";
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
}
