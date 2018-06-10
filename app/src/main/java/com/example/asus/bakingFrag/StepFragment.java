package com.example.asus.bakingFrag;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.bakingFrag.Adapter.Recipes;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_STEP = "step";
    private static final String ARG_NUMBER = "number";
    // The key for the intent
    public static final String STEP_KEY = "step";
    public static final String POSITION_KEY = "position";
    public static final String NAME_KEY = "name";
    public static final String PLAY_WHEN_READY="playWhenReady";
    public static final String CURRENT_WINDOW="window";
    public static final String PLAY_POSITION="playbackPosition";
    private Recipes.Steps mStep;
    private String mVideoUrl;
    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady;
    private TextView mEmptyStateTV;
    private int mNumberStep;
    private View mLoadingIndicator;



    public StepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param step Parameter 1.
     * @param numberStep Parameter 2.
     * @return A new instance of fragment StepFragment.
     */
    public static StepFragment newInstance(Recipes.Steps step, int numberStep) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        args.putInt(ARG_NUMBER, numberStep);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate called");
        if (getArguments() != null) {
            mStep = getArguments().getParcelable(ARG_STEP);
            mNumberStep = getArguments().getInt(ARG_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.i("onCreateView called");
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        mEmptyStateTV = rootView.findViewById(R.id.empty_view);
        mLoadingIndicator = rootView.findViewById(R.id.loading_spinner);
        TextView numberTV = rootView.findViewById(R.id.step_number_detail);
        numberTV.setText(String.valueOf(mNumberStep + 1));
        String shortDescr = mStep.getShortDescription();
        Timber.i("Descr: " + shortDescr);
        TextView shortTV = rootView.findViewById(R.id.step_short_descr);
        shortTV.setText(shortDescr);
        mVideoUrl = mStep.getVideoURL();
        Timber.i("Video: " + mVideoUrl);
        mPlayerView = rootView.findViewById(R.id.player_view);
       if (savedInstanceState != null){
           playWhenReady=savedInstanceState.getBoolean(PLAY_WHEN_READY);
           playbackPosition= savedInstanceState.getLong(PLAY_POSITION);
           currentWindow=savedInstanceState.getInt(CURRENT_WINDOW);
          // mPlayer.setPlayWhenReady(playWhenReady);
       //  mPlayer.seekTo(currentWindow, playbackPosition);
        }
            // Get a reference to the ConnectivityManager to check state of network connectivity
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        // If there is a network connection, start the player
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            if (!mVideoUrl.isEmpty()) {
                mLoadingIndicator.setVisibility(View.GONE);
                if(mPlayer==null) {
                    initializePlayer();
                }
            } else {
                //hide load indicator and player
                mLoadingIndicator.setVisibility(View.GONE);
                mPlayerView.setVisibility(View.GONE);
            }
        } else {// Otherwise, display error.
            mLoadingIndicator.setVisibility(View.GONE);
            // Display no internet connection message
            if (!mVideoUrl.isEmpty()) {
                mEmptyStateTV.setText(R.string.no_internet_connection);
            } else {
                mPlayerView.setVisibility(View.GONE);
            }

        }

        String description=mStep.getDescription();
        TextView descrTV=rootView.findViewById(R.id.description_tv);
        descrTV.setText(description);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.i("onSaveInstanceState called");
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            outState.putLong("PLAYER_POSITION", mPlayer.getCurrentPosition());
            outState.putBoolean("PLAY_WHEN_READY",  mPlayer.getPlayWhenReady());
            Timber.i("saved state play ready: "+ mPlayer.getPlayWhenReady());
            outState.putInt("CURRENT_WINDOW", mPlayer.getCurrentWindowIndex());
        }


    }

    /**
     * Initialize ExoPlayer.
     */
    private void initializePlayer() {
        Timber.i("initializePlayer called");
        playWhenReady = true;
        playbackPosition=0;
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(playWhenReady);
            mPlayer.seekTo(currentWindow, playbackPosition);
            Uri uri = Uri.parse(mVideoUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            mPlayer.prepare(mediaSource, true, false);

    }

    /*
     * Build MediaSource for the given uri. It recognizes mp4 file and play it.
     * @param uri The URI of the sample to play.
     */
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-bakingFrag")).
                createMediaSource(uri);
    }
    private void releasePlayer() {
        if (mPlayer != null) {
            playbackPosition = mPlayer.getCurrentPosition();
            currentWindow = mPlayer.getCurrentWindowIndex();
            playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy called");
        releasePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("onDetach called");
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("onDestroyView called");
        releasePlayer();
    }

    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("onStop called");
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


   @Override
    public void onPause() {
       super.onPause();
       Timber.i("onPause called");
       if (Util.SDK_INT <= 23) {
           releasePlayer();
       }
   }



}
