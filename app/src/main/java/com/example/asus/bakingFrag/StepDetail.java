package com.example.asus.bakingFrag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.bakingFrag.Adapter.Recipes;

public class StepDetail extends AppCompatActivity {
    // The key for the intent
    public static final String STEP_KEY = "step";
    public static final String POSITION_KEY = "position";
    public static final String NAME_KEY = "name";
    private Recipes.Steps mStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        // Receive the intent from RecipeDetail Activity by Parcelable
        // for the step selected in the Recycler
        mStep = getIntent().getParcelableExtra(STEP_KEY);
        int numberStep = getIntent().getIntExtra(POSITION_KEY, 0);
        String name = getIntent().getStringExtra(StepDetail.NAME_KEY);
        setTitle(name + " Step");
        if (savedInstanceState == null) {
            // Create StepFragment and send stepList and name to it.
            StepFragment stepFragment = StepFragment.newInstance(mStep, numberStep);
            // Get the FragmentManager and start a transaction, then add the fragment.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_frag_container, stepFragment,"StepFragment")
                    .commit();
        }

    }


}
