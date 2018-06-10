package com.example.asus.bakingFrag;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.bakingFrag.Adapter.IngredientsAdapter;
import com.example.asus.bakingFrag.Adapter.Recipes;
import com.example.asus.bakingFrag.Adapter.StepsAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { MasterListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MasterListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterListFragment extends Fragment {

    private static final String STEPS_LIST = "steps_list";
    private static final String NAME = "recipe_name";
    private static final String RECYCLER_STATE_KEY = "state_key";
    private OnFragmentInteractionListener mListener;

    private Recipes mRecipe;
    private RecyclerView mRecyclerIngred;
    private RecyclerView mRecyclerStep;
    private IngredientsAdapter mAdapter;
    private StepsAdapter mStepsAdapter;
    private List<Recipes.Steps> mStepsList;
    private String mName;
    private LinearLayoutManager mLayoutSteps;
    private int mLastFirstVisiblePosition;

    public MasterListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stepsList  Parameter 1.
     * @param recipeName Parameter 2.
     * @return A new instance of fragment MasterListFragment.
     */
    public static MasterListFragment newInstance(List<Recipes.Steps> stepsList, String recipeName) {
        MasterListFragment fragment = new MasterListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS_LIST, (ArrayList<? extends Parcelable>) stepsList);
        args.putString(NAME, recipeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStepsList = getArguments().getParcelableArrayList(STEPS_LIST);
            mName = getArguments().getString(NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        //Set Recyclerview and layout to position list items
        mRecyclerStep = rootView.findViewById(R.id.recycler_steps);
        mLayoutSteps = new LinearLayoutManager(getContext());
        mRecyclerStep.setLayoutManager(mLayoutSteps);
        mRecyclerStep.addItemDecoration(new DividerItemDecoration(mRecyclerStep.getContext(), mLayoutSteps.getOrientation()));
        mRecyclerStep.setHasFixedSize(true);
        mStepsAdapter = new StepsAdapter(getContext(), mStepsList);
        mRecyclerStep.setAdapter(mStepsAdapter);
        //mRecyclerStep.setAdapter(mStepsAdapter);

        if(mLastFirstVisiblePosition!=RecyclerView.NO_POSITION){
            mLayoutSteps.scrollToPositionWithOffset(mLastFirstVisiblePosition, 0);
        }
        // Set up the Listener to click on Item row in RecyclerView
        mStepsAdapter.setOnStepClickListener(new StepsAdapter.OnStepClickListener() {
            @Override
            public void onStepClickListener(View itemView, int position) {
                Recipes.Steps stepClicked = mStepsList.get(position);
                if (mListener != null) {
                    mListener.onFragmentInteraction(stepClicked, position);

                }
            }
        });

        return rootView;
    }

    /**
     * to save the state of RecyclerView scroll position.
     * https://stackoverflow.com/questions/35054974/how-to-retain-the-scrolled-position-of-a-recycler-view-on-back-press-from-anothe
     * https://stackoverflow.com/questions/27816217/how-to-save-recyclerviews-scroll-position-using-recyclerview-state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        mLastFirstVisiblePosition = mLayoutSteps.findFirstVisibleItemPosition();
        outState.putInt(RECYCLER_STATE_KEY, mLastFirstVisiblePosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Method to start RecipeDetail Activity when an item row in the recycler is clicked.
     * This activity shows details on the movie selected
     *
     * @param selectedStep step selected to view details
     * @param position     item position of the clicked step
     * @param name         of the recipe steps
     */
    private void startStepDetail(Recipes.Steps selectedStep, int position, String name) {
        Intent intent = new Intent(getContext(), StepDetail.class);
        intent.putExtra(StepDetail.STEP_KEY, selectedStep);
        intent.putExtra(StepDetail.POSITION_KEY, position);
        intent.putExtra(StepDetail.NAME_KEY, name);
        startActivity(intent);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Recipes.Steps selectedStep, int numberStep);
    }
}
