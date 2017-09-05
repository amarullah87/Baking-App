package com.amarullah87.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amarullah87.bakingapp.services.Recipe;
import com.amarullah87.bakingapp.services.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 24/08/17.
 */

public class RecipeStepDetailFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.mediaPlayer) SimpleExoPlayerView mediaPlayer;
    @BindView(R.id.txtDescription) TextView description;
    @BindView(R.id.prevStep) ImageButton prevStep;
    @BindView(R.id.nextStep) ImageButton nextStep;

    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private ArrayList<Step> steps = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Handler handler;
    private int index;
    private String videoURL;
    String recipeName;

    public RecipeStepDetailFragment() {
    }

    private ListItemClickListener itemClickListener;
    public interface ListItemClickListener {
        void onListItemClick(List<Step> stepList, int stepIndex, String recipeName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        handler = new Handler();
        bandwidthMeter = new DefaultBandwidthMeter();
        itemClickListener = (RecipeDetailActivity)getActivity();

        if(savedInstanceState != null){
            steps = savedInstanceState.getParcelableArrayList("selected_step");
            index = savedInstanceState.getInt("index");
            recipeName = savedInstanceState.getString("title");
        }else{
            steps = getArguments().getParcelableArrayList("selected_step");

            if (steps!=null) {
                steps = getArguments().getParcelableArrayList("selected_step");
                index = getArguments().getInt("index");
                recipeName = getArguments().getString("title");
            }else {
                recipes = getArguments().getParcelableArrayList("selected_recipe");
                steps = (ArrayList<Step>) recipes.get(0).getSteps();
                index = 0;
            }
        }

        videoURL = steps.get(index).getVideoURL();
        description.setText(steps.get(index).getDescription());
        description.setVisibility(View.VISIBLE);
        mediaPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        if (view.findViewWithTag("600dp_port_step_detail") != null){
            recipeName = ((RecipeDetailActivity) getActivity()).recipeName;
            ((RecipeDetailActivity) getActivity()).getSupportActionBar().setTitle(recipeName);
        }

        if (!videoURL.isEmpty()){
            if(view.findViewWithTag("600dp_land_step_detail") != null){
                getActivity().findViewById(R.id.fragment_container).setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                mediaPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            }else if(isLandscapeMode(getActivity())){
                description.setVisibility(View.GONE);
            }

            initPlayre(Uri.parse(steps.get(index).getVideoURL()));
        }else{
            player = null;
            mediaPlayer.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.ic_videocam_off_white));
            mediaPlayer.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        }

        nextStep.setOnClickListener(this);
        prevStep.setOnClickListener(this);

        return view;
    }

    private boolean isLandscapeMode(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (player!=null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player!=null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player != null){
            player.stop();
            player.release();
        }
    }

    private void initPlayre(Uri parse) {
        if (player == null){
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mediaPlayer.setPlayer(player);

            String userAgent = Util.getUserAgent(getContext(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(parse, new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.nextStep:
                if (steps.get(index).getId() < steps.get(steps.size()-1).getId()) {
                    if (player!=null){
                        player.stop();
                    }
                    itemClickListener.onListItemClick(steps, steps.get(index).getId() + 1, recipeName);
                }else{
                    Toast.makeText(getContext(), "You are in the Last step", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.prevStep:
                if (steps.get(index).getId() > 0) {
                    if (player!=null){
                        player.stop();
                    }
                    itemClickListener.onListItemClick(steps,steps.get(index).getId() - 1,recipeName);
                }else{
                    Toast.makeText(getActivity(), "You are in the First step", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("selected_step", steps);
        outState.putInt("index", index);
        outState.putString("title", recipeName);
    }
}
