package com.example.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    //private String[] mWeatherData;
    private final Context mContext;

    private final ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    private boolean mUseTodayLayout;

    private Cursor mCursor;

//    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
//        mClickHandler = clickHandler;
//    }
    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

//    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
//        public final TextView mWeatherTextView;
//
//        public ForecastAdapterViewHolder(View view) {
//            super(view);
//            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
//            view.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            String weatherForDay = mWeatherData[adapterPosition];
//            mClickHandler.onClick(weatherForDay);
//        }
//    }
//    @Override
//    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//
//        View view = LayoutInflater
//                .from(mContext)
//                .inflate(R.layout.forecast_list_item, viewGroup, false);
//
//        view.setFocusable(true);
//
//        return new ForecastAdapterViewHolder(view);
//    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new ForecastAdapterViewHolder(view);
    }

//    @Override
//    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        Context context = viewGroup.getContext();
//        int layoutIdForListItem = R.layout.forecast_list_item;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        boolean shouldAttachToParentImmediately = false;
//
//        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
//        return new ForecastAdapterViewHolder(view);
//    }

//    @Override
//    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
////        String weatherForThisDay = mWeatherData[position];
////        forecastAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
//        mCursor.moveToPosition(position);
//
//        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
//
//        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
//
//        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
//        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
//
//        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
//
//        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
//
//        String highAndLowTemperature =
//                SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius);
//
//        String weatherSummary = dateString + " - " + description + " - " + highAndLowTemperature;
//
//        forecastAdapterViewHolder.weatherSummary.setText(weatherSummary);
//    }
//    @Override
//    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
//        mCursor.moveToPosition(position);
//
//        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
//        int weatherImageId;
//
//        weatherImageId = SunshineWeatherUtils
//                .getSmallArtResourceIdForWeatherCondition(weatherId);
//
//        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);
//
//        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
//        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
//
//        forecastAdapterViewHolder.dateView.setText(dateString);
//
//        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
//        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);
//
//        forecastAdapterViewHolder.descriptionView.setText(description);
//        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionA11y);
//
//        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
//        String highString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
//        String highA11y = mContext.getString(R.string.a11y_high_temp, highString);
//
//        forecastAdapterViewHolder.highTempView.setText(highString);
//        forecastAdapterViewHolder.highTempView.setContentDescription(highA11y);
//
//        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
//        String lowString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
//        String lowA11y = mContext.getString(R.string.a11y_low_temp, lowString);
//
//        forecastAdapterViewHolder.lowTempView.setText(lowString);
//        forecastAdapterViewHolder.lowTempView.setContentDescription(lowA11y);
//    }
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;

        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_TYPE_TODAY:
                weatherImageId = SunshineWeatherUtils
                        .getLargeArtResourceIdForWeatherCondition(weatherId);
                break;

            case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = SunshineWeatherUtils
                        .getSmallArtResourceIdForWeatherCondition(weatherId);
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);

        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);

        forecastAdapterViewHolder.dateView.setText(dateString);

        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);

        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionA11y);

        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
        String highA11y = mContext.getString(R.string.a11y_high_temp, highString);

        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highA11y);

        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String lowString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
        String lowA11y = mContext.getString(R.string.a11y_low_temp, lowString);

        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowA11y);
    }

    @Override
    public int getItemCount() {
//        if (null == mWeatherData) return 0;
//        return mWeatherData.length;
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {

        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;

        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

//    public void setWeatherData(String[] weatherData) {
//        mWeatherData = weatherData;
//        notifyDataSetChanged();
//    }

//    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        final TextView weatherSummary;
//
//        ForecastAdapterViewHolder(View view) {
//            super(view);
//
//            weatherSummary = (TextView) view.findViewById(R.id.tv_weather_data);
//
//            view.setOnClickListener(this);
//        }
//
////        @Override
////        public void onClick(View v) {
////            String weatherForDay = weatherSummary.getText().toString();
////            mClickHandler.onClick(weatherForDay);
////        }
//        @Override
//        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
//            mClickHandler.onClick(dateInMillis);
//        }
//    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        final ImageView iconView;

        ForecastAdapterViewHolder(View view) {
            super(view);

            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
}