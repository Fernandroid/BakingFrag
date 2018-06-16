package com.example.asus.bakingFrag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.asus.bakingFrag.Adapter.Recipes;
import com.google.gson.Gson;

import java.util.List;

import timber.log.Timber;

public class BakingWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingRemoteViewsFactory(this.getApplicationContext());
    }

    class BakingRemoteViewsFactory implements RemoteViewsFactory{

        private Context mContext;
        private String mName;
        private Recipes mRecipe;
        private List<Recipes.Ingredients> ingredientsList;

        public BakingRemoteViewsFactory(Context context) {
            mContext=context;
        }

        @Override
        public void onCreate() {

        }

        //called on start and when notifyAppWidgetViewDataChanged is called
        @Override
        public void onDataSetChanged() {
            Gson gson=new Gson();
            SharedPreferences sharedPref=mContext.getSharedPreferences(getString(R.string.preferece_file_key),mContext.MODE_PRIVATE);
            String stringRecipe=sharedPref.getString(getString(R.string.file_json_key),null);
            mName=sharedPref.getString(getString(R.string.recipe_name_key),null);
            Timber.i("widget ingredient: " +mName);
            if(stringRecipe!=null){
              mRecipe= gson.fromJson(stringRecipe,Recipes.class);
              ingredientsList=mRecipe.getIngredients();
            }



        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredientsList == null) return 0;
            Timber.i("size ingredient list: "+ingredientsList.size());
            return ingredientsList.size();
        }
        /**
         * This method acts like the onBindViewHolder method in an Adapter
         *
         * @param position The current position of the item in the GridView to be displayed
         * @return The RemoteViews object to display for the provided postion
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (ingredientsList == null || ingredientsList.size() == 0) return null;
            //Get ingredient
            Recipes.Ingredients ingredient=ingredientsList.get(position);
            //Get a layout item widget and set text
            RemoteViews views=new RemoteViews(mContext.getPackageName(),R.layout.item_widget_ingredient);
            views.setTextViewText(R.id.ingredient,ingredient.getIngredient());
            views.setTextViewText(R.id.widget_measure,ingredient.getMeasure());
            views.setTextViewText(R.id.widget_quantity,String.valueOf(ingredient.getQuantity()));
            // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
            Bundle extras = new Bundle();
            extras.putParcelable(RecipeDetail.RECIPE_KEY, mRecipe);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.item_widget, fillInIntent);
            return views;
        }




        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;// Treat all items in the GridView the same
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
