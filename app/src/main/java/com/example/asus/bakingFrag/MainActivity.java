package com.example.asus.bakingFrag;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.asus.bakingFrag.Adapter.Recipes;
import com.example.asus.bakingFrag.Adapter.RecipesAdapter;
import com.example.asus.bakingFrag.Api.BakingApi;
import com.example.asus.bakingFrag.Api.EndPoint;
import com.example.asus.bakingFrag.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecipesAdapter mRecipesAdapter;
    List<Recipes> mRecipesList;
    TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        //Set Recyclerview and layout to position list items
        //https://stackoverflow.com/questions/31242812/how-can-a-divider-line-be-added-in-an-android-recyclerview

        //if the layout is for phone
        if(findViewById(R.id.recycler)!=null){
            //then set up the recycler as vertical linear way
            mRecyclerView = findViewById(R.id.recycler);
            LinearLayoutManager layout=new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layout);
            //Set divider between itmes in Recyclerview
            DividerItemDecoration itemDivider = new DividerItemDecoration(mRecyclerView.getContext(),
                    layout.getOrientation() );
            mRecyclerView.addItemDecoration(itemDivider);
        }else{
            //if the layout is for tablet in landscape orientation
            mRecyclerView = findViewById(R.id.recycler_sw600_land);
            // set up GridLayoutManager to position the items in 2 columns
            GridLayoutManager gridLayout = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayout);
            //Set divider between itmes in Recyclerview
            DividerItemDecoration itemDivider = new DividerItemDecoration(mRecyclerView.getContext(),
                    gridLayout.getOrientation() );
            mRecyclerView.addItemDecoration(itemDivider);
        }


        // This setting improves performance since items in recyclerView do not change the child layout size
        mRecyclerView.setHasFixedSize(true);
        // Create RecipesAdapter and attach it to RecyclerView
        mRecipesAdapter = new RecipesAdapter(this);
        mRecyclerView.setAdapter(mRecipesAdapter);

        //Download recipes data from internet by retrofit
        EndPoint api = BakingApi.getRequest();
        Call<ArrayList<Recipes>> response=api.getRecipes();
        response.enqueue(new Callback<ArrayList<Recipes>>() {

            @Override
            public void onResponse(Call<ArrayList<Recipes>> call, Response<ArrayList<Recipes>> response) {
                Timber.i("Retrofit Response");
                if (response.isSuccessful()){
                    mRecipesList=response.body();
                    mRecipesAdapter.setRecipesData(mRecipesList);
                    Timber.i("Size of response "+String.valueOf(mRecipesList.size()));
                    View loadingIndicator = findViewById(R.id.loading_spinner);
                    loadingIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipes>> call, Throwable t) {
                Timber.e(t);
                View loadingIndicator = findViewById(R.id.loading_spinner);
                if(!isInternetAvailable()){
                    loadingIndicator.setVisibility(View.GONE);
                    // Display no internet connection message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }else {
                    loadingIndicator.setVisibility(View.GONE);
                    //mEmptyStateTextView.setVisibility(View.VISIBLE);
                 //   mEmptyStateTextView.setText(getString(R.string.no_download_data));
                }
            }
        });

     /*  try{
            mRecipesList=Utils.extractDataFromJson(this);
        } catch (IOException e) {
            Timber.e(e);
        }*/

        // Set up the Listener to click on Item row in RecyclerView
        mRecipesAdapter.setOnItemClickListener(new RecipesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // When one row in Recycler is clicked, this method build intent message emboding
                // the recipe selected and send it to details acitivity by Parcelable
                Recipes recipe=mRecipesList.get(position);
                startRecipeDetail(recipe);
            }
        });


    }

    /**
     * Method to start RecipeDetail Activity when an item row in the recycler is clicked.
     * This activity shows details on the movie selected
     * @param selectedRecipe recipe selected to view details
     */
    private void startRecipeDetail( Recipes selectedRecipe) {
        Intent intent = new Intent(this, RecipeDetail.class);
        intent.putExtra(RecipeDetail.RECIPE_KEY, selectedRecipe);
        startActivity(intent);
    }

    /* Check internet availability */
    private   boolean isInternetAvailable() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}


