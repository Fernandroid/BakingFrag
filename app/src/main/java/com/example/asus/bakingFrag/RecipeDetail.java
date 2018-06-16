package com.example.asus.bakingFrag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.asus.bakingFrag.Adapter.IngredientsAdapter;
import com.example.asus.bakingFrag.Adapter.Recipes;
import com.example.asus.bakingFrag.Adapter.StepsAdapter;
import com.google.gson.Gson;

import java.util.List;

import timber.log.Timber;

public class RecipeDetail extends AppCompatActivity implements MasterListFragment.OnFragmentInteractionListener{
    // The key for the intent
    public static final String RECIPE_KEY = "RECIPE";
    private Recipes mRecipe;
    private RecyclerView mRecyclerIngred;
    private RecyclerView mRecyclerStep;
    private IngredientsAdapter mAdapter;
    private StepsAdapter mStepsAdapter;
    private List<Recipes.Steps> mStepsList;
    private String mName;
    private boolean mTwoPane;
    private boolean mTwoPaneLand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Is the layout for two pane available? If so, set mTwoPane to true.
        if (findViewById(R.id.recipe_detail_sw600) != null ||findViewById(R.id.recipe_detail_sw600_land)!=null ) {
            mTwoPane = true;
        }else{
            mTwoPane=false;
        }

        // Receive the intent from MainActivity by Parcelable
        // for the recipe selected in the Recycler
        mRecipe = getIntent().getParcelableExtra(RECIPE_KEY);
        mName= mRecipe.getName();
        setTitle(mName);
        int servings=mRecipe.getServings();
        TextView servingTV=findViewById(R.id.serving_tv);
        servingTV.setText(String.valueOf(servings));
        //Save recipe in SharedPreferences
        saveIngredientList(mName,mRecipe);

        List<Recipes.Ingredients> ingredientsList=mRecipe.getIngredients();
        mStepsList=mRecipe.getSteps();
        //Set Recyclerview and layout to position list items
        mRecyclerIngred =findViewById(R.id.recycler_ingredients);
        //Attach LinearLayoutManager to Recycler
        LinearLayoutManager layoutIngred=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerIngred.setLayoutManager(layoutIngred);
        //Set divider between itmes in Recyclerview
        DividerItemDecoration itemDivider = new DividerItemDecoration(mRecyclerIngred.getContext(),
            layoutIngred.getOrientation());
        mRecyclerIngred.addItemDecoration(itemDivider);
        mRecyclerIngred.setHasFixedSize(true);
        // Create Adapter and attach it to RecyclerView
        mAdapter=new IngredientsAdapter(this,ingredientsList);
        mRecyclerIngred.setAdapter(mAdapter);

        /*if we're being restored from a previous state,
        // then we don't need to do anything
        Check if the Fragment already exists before creating a new one.
        This is because when there is a configuration change, the Fragment isn't really destroyed so we don't really need to instantiate
         a new Fragment instance, else we could end up with overlapping fragments.
         Instead the system will re-use the existing one and add it back to the Activity when the Activity is recreated. */
        if(savedInstanceState == null){
            // Create MaterListFragment if the activity is started for the first time (initially launched),
            // and send stepList and name to it.
            MasterListFragment masterList=MasterListFragment.newInstance(mStepsList,mName);
            // Get the FragmentManager and start a transaction, then add the fragment.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.master_list_fragment,masterList)
                    .commit();

        }
    }

    /**
     * Method to start RecipeDetail Fragment when an item row in the MasterListFragment recycler is clicked.
     * This activity shows details on the movie selected
     * @param selectedStep step selected to view details
     * @param numberStep item position of the clicked step
     */
    @Override
    public void onFragmentInteraction(Recipes.Steps selectedStep, int numberStep) {

        if (mTwoPane) {
            // Create new instance of StepDetail fragment and add it to
            // the activity using a fragment transaction.
            StepFragment fragment = StepFragment.newInstance(selectedStep,numberStep);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_frag_container, fragment)// .addToBackStack(null)
                    .commit();
        }else {
            startStepDetail(selectedStep,numberStep, mName);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Method to start RecipeDetail Activity when an item row in the recycler is clicked.
     * This activity shows details on the movie selected
     * @param selectedStep step selected to view details
     * @param position item position of the clicked step
     * @param name of the recipe steps
     */
    private void startStepDetail( Recipes.Steps selectedStep,int position, String name) {
        Intent intent = new Intent(this, StepDetail.class);
        intent.putExtra(StepDetail.STEP_KEY, selectedStep);
        intent.putExtra(StepDetail.POSITION_KEY,position);
        intent.putExtra(StepDetail.NAME_KEY,name);
        startActivity(intent);
    }

    /* Save list ingredient in SharedPreferences file to display in app Widget*/
    private void saveIngredientList(String recipeName, Recipes recipes){
        Timber.i("save ingredient");
        Gson gson=new Gson();
        String fileJson=gson.toJson(recipes);
        SharedPreferences sharedPref=getSharedPreferences(getString(R.string.preferece_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString(getString(R.string.file_json_key),fileJson);
        editor.putString(getString(R.string.recipe_name_key),recipeName);
        editor.commit();
    }

}
