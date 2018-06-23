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
import android.util.Log;
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
    View mLoadingIndicator;
    private int mLastFirstVisiblePosition;
    private static final String RECYCLER_STATE_KEY = "state_key";
    private LinearLayoutManager mLayout;
    private GridLayoutManager mGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        mLoadingIndicator = findViewById(R.id.loading_spinner_activity);
        mEmptyStateTextView=findViewById(R.id.empty_view);
        //if the layout is for phone
        if(findViewById(R.id.recycler)!=null){
            //then set up the recycler as vertical linear way
            mRecyclerView = findViewById(R.id.recycler);
            mLayout=new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayout);
            //Set divider between itmes in Recyclerview
            //https://stackoverflow.com/questions/31242812/how-can-a-divider-line-be-added-in-an-android-recyclerview
            DividerItemDecoration itemDivider = new DividerItemDecoration(mRecyclerView.getContext(),
                    mLayout.getOrientation() );
            mRecyclerView.addItemDecoration(itemDivider);
        }else{
            //if the layout is for tablet in landscape orientation
            mRecyclerView = findViewById(R.id.recycler_sw600_land);
            // set up GridLayoutManager to position the items in 2 columns
            mGridLayout = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(mGridLayout);
            //Set divider between itmes in Recyclerview
            DividerItemDecoration itemDivider = new DividerItemDecoration(mRecyclerView.getContext(),
                    mGridLayout.getOrientation() );
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
                    mLoadingIndicator.setVisibility(View.GONE);
                    if(findViewById(R.id.recycler)!=null){
                        mLayout.scrollToPositionWithOffset(mLastFirstVisiblePosition, 0);
                    }else {
                        mGridLayout.scrollToPositionWithOffset(mLastFirstVisiblePosition, 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipes>> call, Throwable t) {
                Timber.e(t);
                if(!isInternetAvailable()){
                    mLoadingIndicator.setVisibility(View.GONE);
                    // Display no internet connection message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }else {
                    mLoadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(getString(R.string.no_download_data));
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

    /**
     * to save the state of RecyclerView scroll position.
     * https://stackoverflow.com/questions/35054974/how-to-retain-the-scrolled-position-of-a-recycler-view-on-back-press-from-anothe
     * https://stackoverflow.com/questions/27816217/how-to-save-recyclerviews-scroll-position-using-recyclerview-state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(findViewById(R.id.recycler)!=null){
            mLastFirstVisiblePosition = mLayout.findFirstVisibleItemPosition();
        }else {
            mGridLayout.findFirstVisibleItemPosition();
        }
        outState.putInt(RECYCLER_STATE_KEY, mLastFirstVisiblePosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Timber.i( "onRestoreInstanceState called ");
        mLastFirstVisiblePosition = savedInstanceState.getInt(RECYCLER_STATE_KEY);
        if(findViewById(R.id.recycler)!=null){
            mLayout.scrollToPositionWithOffset(mLastFirstVisiblePosition,0);
        }else {
            mGridLayout.scrollToPositionWithOffset(mLastFirstVisiblePosition,0);
        }
    }
}


