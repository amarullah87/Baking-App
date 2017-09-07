package com.amarullah87.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amarullah87.bakingapp.adapters.RecipeStepAdapter;
import com.amarullah87.bakingapp.services.Recipe;
import com.amarullah87.bakingapp.services.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 18/08/17.
 */

public class RecipeDetailActivity extends AppCompatActivity
    implements RecipeStepAdapter.ItemClickListener, RecipeStepDetailFragment.ListItemClickListener{

    @BindView(R.id.rootLayout) CoordinatorLayout rootLayout;
    private static final String STACK_DETAIL = "STACK_DETAIL";
    private FragmentManager manager;
    String recipeName;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {

            if(getIntent().getExtras() != null) {
                Bundle intentBundle = getIntent().getExtras();

                ArrayList<Recipe> items = intentBundle.getParcelableArrayList("selected_recipe");
                if (items != null) {
                    recipeName = items.get(0).getName();
                }

                manager = getSupportFragmentManager();

                RecipeDetailFragment detailFragment = new RecipeDetailFragment();
                detailFragment.setArguments(intentBundle);
                manager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(
                                R.id.fragment_step,
                                detailFragment,
                                detailFragment.getTag())
                        .commit();

                if (rootLayout.getTag() != null && rootLayout.getTag().equals("tablet-land")) {

                    Log.e("Tablet-Land: ", "Uye!");
                    RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
                    stepDetailFragment.setArguments(intentBundle);
                    manager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(
                                    R.id.fragment_container,
                                    stepDetailFragment,
                                    stepDetailFragment.getTag())
                            .addToBackStack(STACK_DETAIL)
                            .commit();
                }
            }else {
                Snackbar.make(rootLayout, R.string.no_intent_extra, Snackbar.LENGTH_LONG).show();
            }
        }else{
            recipeName = savedInstanceState.getString("title");
        }

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(recipeName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onItemClick(List<Step> steps, int index, String recipeName) {
        RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
        manager = getSupportFragmentManager();
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(recipeName);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selected_step", (ArrayList<Step>) steps);
        bundle.putInt("index", index);
        bundle.putString("title", recipeName);
        stepDetailFragment.setArguments(bundle);

        if(findViewById(R.id.fragment_container) != null){
            manager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(
                            R.id.fragment_container,
                            stepDetailFragment,
                            stepDetailFragment.getTag())
                    .addToBackStack(STACK_DETAIL)
                    .commit();
        }else{
            manager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(
                            R.id.fragment_step,
                            stepDetailFragment,
                            stepDetailFragment.getTag())
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", recipeName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;

            case R.id.addWidget:
                RecipeDetailFragment fragment = (RecipeDetailFragment) manager.findFragmentById(R.id.fragment_step);
                fragment.addToWidget();

                Toast.makeText(this, "Added To Widget", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    @Override
    public void onListItemClick(List<Step> stepList, int stepIndex, String recipeName) {
        RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
        manager = getSupportFragmentManager();
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(recipeName);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selected_step", (ArrayList<Step>) stepList);
        bundle.putInt("index", stepIndex);
        bundle.putString("title", recipeName);
        stepDetailFragment.setArguments(bundle);

        if(findViewById(R.id.fragment_container) != null){
            manager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(
                            R.id.fragment_container,
                            stepDetailFragment,
                            stepDetailFragment.getTag())
                    .addToBackStack(STACK_DETAIL)
                    .commit();
        }else{
            manager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(
                            R.id.fragment_step,
                            stepDetailFragment,
                            stepDetailFragment.getTag())
                    .commit();
        }
    }
}
