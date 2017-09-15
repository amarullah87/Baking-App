package com.amarullah87.bakingapp.services;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Baking App Project Android Fast Track Nanodegree - Created By Irfan Apandhi, September 2017
 * REST Recie
 */

public interface RestAPI {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getAllRecipe();
}
