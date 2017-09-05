package com.amarullah87.bakingapp.services;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by apandhis on 18/08/17.
 */

public interface RestAPI {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getAllRecipe();
}
