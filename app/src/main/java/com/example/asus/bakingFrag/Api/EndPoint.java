package com.example.asus.bakingFrag.Api;

import com.example.asus.bakingFrag.Adapter.Recipes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EndPoint {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipes>> getRecipes();

}
