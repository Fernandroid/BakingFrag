package com.example.asus.bakingFrag.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.asus.bakingFrag.Adapter.Recipes;
import com.example.asus.bakingFrag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Utils {



    public static List<Recipes> extractDataFromJson(Context context) throws IOException {

        String jsonFile = "";
        InputStream inputStream=null;

        try{
            inputStream = context.getResources().openRawResource(R.raw.baking);
            Timber.i("Open raw data");
            jsonFile=readFromStream(inputStream);
        }catch (IOException e){
            Log.e("Utils","Problem",e);
            Timber.e(e,"Problem reading json file");
        }finally {
            // Close inputStream
            if (inputStream != null) {
                inputStream.close();
            }
        }

        // Create an empty ArrayList that we can start adding recipes
        ArrayList<Recipes> recipesList = new ArrayList<>();


        // Try to parse the JSON_RESPONSE. If there's a problem with the way the JSON is formatted,
        // a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the jsonResponse and build up a list of Earthquake objects with the corresponding data.
            JSONArray results = new JSONArray(jsonFile);
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {

                    //Get the Recipes fields
                    JSONObject currentRecipe = results.optJSONObject(i);
                    String name = currentRecipe.optString("name");
                    String image = currentRecipe.optString("image");
                    int servings = currentRecipe.optInt("servings");
                    Recipes recipes=new Recipes(name,image,servings);
                    //Get the Ingredients fields
                    JSONArray ingredients = currentRecipe.optJSONArray("ingredients");
                    if(ingredients!=null) {
                        ArrayList<Recipes.Ingredients> ingredsList = new ArrayList<>();
                        for (int j = 0; j < ingredients.length(); j++) {
                            Timber.i(name + " number ingredients: "+ ingredients.length());
                            JSONObject currentIngred = ingredients.getJSONObject(j);
                            int quantity = currentIngred.optInt("quantity");
                            String measure=currentIngred.optString("measure");
                            String ingredient=currentIngred.optString("ingredient");
                            Recipes.Ingredients ingredientObj=new Recipes.Ingredients(quantity,measure,ingredient);
                            ingredsList.add(ingredientObj);
                            recipes.setIngredients(ingredsList);
                        }

                    }
                    //Get the Steps fields
                    JSONArray steps = currentRecipe.optJSONArray("steps");
                    if(steps!=null){
                        ArrayList<Recipes.Steps> stepsList = new ArrayList<>();
                        for (int s=0;s<steps.length();s++){
                            JSONObject currentStep=steps.optJSONObject(s);
                            String shortDesc=currentStep.optString("shortDescription");
                            String description=currentStep.optString("description");
                            String videoURL=currentStep.optString("videoURL");
                            String thumbnailURL=currentStep.optString("thumbnailURL");
                            Recipes.Steps stepObj=new Recipes.Steps(shortDesc,description,videoURL,thumbnailURL);
                            stepsList.add(stepObj);
                            recipes.setSteps(stepsList);
                    }
                    }


                   recipesList.add(recipes);

                }
            }else{
                Timber.i("No file Json");
            }


        } catch (JSONException ex) {
            Timber.e("Problem parsing movie Json response");
        }

        // Return the list of Recipes
        return recipesList;
}

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON file from The Movie DB server
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Builds URL object to query the movie poster from Movie DB server given the resolution.
     *
     * @param imageSize the resolution to load image.
     * @param imageUrl the URL to get the image.
     * @return URL to use to get imager.
     */
    public static Uri createUrlImage(String imageSize, String imageUrl) {

        Uri builtUri = Uri.parse(imageUrl).buildUpon()
                .appendEncodedPath(imageSize)
                .build();

        Timber.i("Built URI " + builtUri);

        return builtUri;
    }
}





