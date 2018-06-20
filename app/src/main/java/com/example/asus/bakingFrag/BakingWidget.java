package com.example.asus.bakingFrag;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Timber.i("update widget called");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        // get the last displayed recipe by user in RecipeDetail
        String recipeName= getRecipeName(context);
        if(recipeName!=null){
            // diplay the name of the recipe
            views.setTextViewText(R.id.recipe_name_widget,recipeName);
            // Set the service to act as adapter of ListView
            Intent sendService=new Intent(context,BakingWidgetService.class);
            views.setRemoteAdapter(R.id.appwidget_listview,sendService);
            // Set the RecipeDeail intent to launch when clicked
            Intent appIntent = new Intent(context, RecipeDetail.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.appwidget_listview, appPendingIntent);
        } else {
           views.setTextViewText(R.id.no_data_widget,context.getString(R.string.no_data_widget_key));
           //Set visibility to see the text no data and invisibility to hide recycler
             views.setViewVisibility(R.id.no_data_widget, View.VISIBLE);
             views.setViewVisibility(R.id.appwidget_listview,View.INVISIBLE);
             // Intent to open MainActivity to look recipe list
            Intent openMainActivity = new Intent(context, MainActivity.class);
            PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openMainActivity, 0);
            views.setOnClickPendingIntent(R.id.no_data_widget,openPendingIntent);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    // Get the recipe that user has displayed in RecipeDetail acitivity
    public static String getRecipeName(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPref =context.getSharedPreferences(context.getString(R.string.preferece_file_key), context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.recipe_name_key), null);
    }
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        //when the widget is resized this update the views
        updateAppWidget(context, appWidgetManager, appWidgetId);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.appwidget_listview);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

