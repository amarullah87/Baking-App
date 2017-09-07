package com.amarullah87.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amarullah87.bakingapp.services.Recipe;
import com.amarullah87.bakingapp.services.Step;
import com.google.android.exoplayer2.C;
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
import com.squareup.picasso.Picasso;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 24/08/17.
 */

public class RecipeStepDetailFragment extends Fragment implements View.OnClickListener{

    private static final String SELECTED_POSITION = "last_position";
    @BindView(R.id.mediaPlayer) SimpleExoPlayerView mediaPlayer;
    @BindView(R.id.txtDescription) TextView description;
    @BindView(R.id.prevStep) ImageButton prevStep;
    @BindView(R.id.nextStep) ImageButton nextStep;
    @BindView(R.id.thumbnailPlayer) ImageView thumbnailPlayer;

    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private ArrayList<Step> steps = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Handler handler;
    private int index;
    private String videoURL;
    String recipeName;
    private long position;
    private Uri videoUri;

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

        position = C.TIME_UNSET;
        if(savedInstanceState != null){
            steps = savedInstanceState.getParcelableArrayList("selected_step");
            index = savedInstanceState.getInt("index");
            recipeName = savedInstanceState.getString("title");
            position = savedInstanceState.getLong(SELECTED_POSITION);
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
        videoUri = Uri.parse(steps.get(index).getVideoURL());

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

            initPlayer(Uri.parse(steps.get(index).getVideoURL()));
        }else{
            if(isVideoFile(steps.get(index).getThumbnailURL())){
                mediaPlayer.setVisibility(View.VISIBLE);
                thumbnailPlayer.setVisibility(View.GONE);

                if(view.findViewWithTag("600dp_land_step_detail") != null){
                    getActivity().findViewById(R.id.fragment_container).setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                    mediaPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                }else if(isLandscapeMode(getActivity())){
                    description.setVisibility(View.GONE);
                }

                initPlayer(Uri.parse(steps.get(index).getThumbnailURL()));
            }else{
                player = null;
                mediaPlayer.setVisibility(View.GONE);
                mediaPlayer.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.ic_videocam_off_white));
                mediaPlayer.setLayoutParams(new LinearLayout.LayoutParams(300, 300));

                thumbnailPlayer.setVisibility(View.VISIBLE);
                String imageUrl = steps.get(index).getThumbnailURL();
                if(!Objects.equals(imageUrl, "")){
                    Uri uri = Uri.parse(imageUrl).buildUpon().build();
                    Picasso.with(getActivity())
                            .load(uri)
                            .placeholder(R.drawable.myicon)
                            .into(thumbnailPlayer);
                }
            }

        }

        nextStep.setOnClickListener(this);
        prevStep.setOnClickListener(this);

        return view;
    }

    private boolean isLandscapeMode(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            position = player.getCurrentPosition();
            player.stop();
            player.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(videoUri != null){
            initPlayer(videoUri);
        }
    }

    private void initPlayer(Uri parse) {
        if (player == null){
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(handler, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mediaPlayer.setPlayer(player);

            String userAgent = Util.getUserAgent(getContext(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(parse, new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            if (position != C.TIME_UNSET) player.seekTo(position);
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
        outState.putLong(SELECTED_POSITION, position);
        outState.putParcelableArrayList("selected_step", steps);
        outState.putInt("index", index);
        outState.putString("title", recipeName);
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}
