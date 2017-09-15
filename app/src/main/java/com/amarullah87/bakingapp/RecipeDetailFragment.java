package com.amarullah87.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amarullah87.bakingapp.adapters.IngredientAdapter;
import com.amarullah87.bakingapp.adapters.RecipeStepAdapter;
import com.amarullah87.bakingapp.services.Ingredient;
import com.amarullah87.bakingapp.services.Recipe;
import com.amarullah87.bakingapp.utilities.BakingAppService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Baking App Project Android Fast Track Nanodegree - Created By Irfan Apandhi, September 2017
 * Contain Detail step and Ingredients of Recipe
 */

public class RecipeDetailFragment extends Fragment{
    private ArrayList<Recipe> recipe = new ArrayList<>();
    private List<Ingredient> ingredients;
    private String recipeName;

    private RecipeStepAdapter detailAdapter;
    private IngredientAdapter ingredientAdapter;

    @BindView(R.id.recipe_detail) RecyclerView detailRv;
    @BindView(R.id.recipe_step) RecyclerView stepRv;

    public RecipeDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);

        if(savedInstanceState != null){
            recipe = savedInstanceState.getParcelableArrayList("selected_recipe");
        }else{
            recipe = getArguments().getParcelableArrayList("selected_recipe");
        }

        if (recipe != null) {
            ingredients = recipe.get(0).getIngredients();
            recipeName = recipe.get(0).getName();

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
            detailRv.setLayoutManager(mLayoutManager); stepRv.setLayoutManager(mLayoutManager2);

            ingredientAdapter = new IngredientAdapter(getActivity(), ingredients);
            detailRv.setAdapter(ingredientAdapter);

            detailAdapter = new RecipeStepAdapter((RecipeDetailActivity)getActivity());
            stepRv.setAdapter(detailAdapter);
            detailAdapter.setRecipeDetail(recipe, getContext());

            /*int size = recipe.get(0).getSteps().size();
            Log.e("Step Size: ", String.valueOf(size));*/
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("selected_recipe", recipe);
        outState.putString("title", recipeName);
    }

    public void addToWidget(){
        ArrayList<String> listItem = new ArrayList<>();

        StringBuilder s = new StringBuilder(100);

        int size = ingredients.size();
        Log.e("Ingredients Size: ", String.valueOf(size)) ;
        for (int i = 0; i < size; i++) {
            Ingredient items = ingredients.get(i);
            listItem.add(items.getIngredient() + " - " + items.getQuantity().toString() + " " + items.getMeasure() + "\n");
            //listItem.add(items.getIngredient()+ "\n");

            s.append(items.getIngredient()).append(" - ").append(items.getQuantity().toString()).append(" ").append(items.getMeasure()).append("\n");
        }
        Log.e("Widget Data: ", String.valueOf(s));
        BakingAppService.bakingService(getContext(), listItem, recipeName);
    }
}
