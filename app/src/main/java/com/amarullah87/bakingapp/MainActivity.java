package com.amarullah87.bakingapp;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amarullah87.bakingapp.adapters.RecipesAdapter;
import com.amarullah87.bakingapp.services.Recipe;
import com.amarullah87.bakingapp.services.RestAPI;
import com.amarullah87.bakingapp.utilities.Configs;
import com.amarullah87.bakingapp.utilities.InternetConnection;
import com.amarullah87.bakingapp.idlingresource.SimpleIdlingResource;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.ItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecipesAdapter adapter;
    boolean doubleBackToExitPressedOnce = false;
    private RecyclerView.LayoutManager manager;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.parentPanel) CoordinatorLayout parent;
    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.rootLayout) RelativeLayout rootLayout;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.app_name);
        }

        if(rootLayout.getTag() != null && rootLayout.getTag().equals("land")){
            GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
            recyclerView.setLayoutManager(mLayoutManager);
        }else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
        }

        adapter = new RecipesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getIdlingResource();
        loadRecipes();
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    private void loadRecipes() {
        progressBar.setVisibility(View.VISIBLE);

        if(InternetConnection.checkConnection(getApplicationContext())){
            RestAPI restAPI = Configs.getRetrofit().create(RestAPI.class);
            Call<ArrayList<Recipe>> call = restAPI.getAllRecipe();

            call.enqueue(new Callback<ArrayList<Recipe>>() {
                @Override
                public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                    Integer statusCode = response.code();
                    Log.v("status code: ", statusCode.toString());

                    progressBar.setVisibility(View.GONE);
                    if(response.isSuccessful() && statusCode != 400) {
                        if (response.body() != null) {
                            ArrayList<Recipe> recipes = response.body();

                            Bundle recipesBundle = new Bundle();
                            recipesBundle.putParcelableArrayList("Recipes", recipes);

                            adapter.setRecipeData(recipes, getApplicationContext());
                            if(mIdlingResource != null){
                                mIdlingResource.setIdleState(true);
                            }
                        }else{
                            Snackbar.make(parent, R.string.no_data, Snackbar.LENGTH_LONG).show();
                        }
                    }else{
                        Snackbar.make(parent, R.string.oops_wrong, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                    Log.e("http fail: ", t.getMessage());
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            Snackbar.make(parent, R.string.oops_network, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(Recipe index) {
        Bundle selectedRecipeBundle = new Bundle();
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(index);
        selectedRecipeBundle.putParcelableArrayList("selected_recipe", selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(selectedRecipeBundle);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
        }else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to EXIT.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
