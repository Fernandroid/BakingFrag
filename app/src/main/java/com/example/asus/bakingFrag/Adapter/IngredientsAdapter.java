package com.example.asus.bakingFrag.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.bakingFrag.R;

import java.util.List;

import timber.log.Timber;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    private Context mContext;
    private List<Recipes.Ingredients> mIngredients;

    public IngredientsAdapter(Context context,List<Recipes.Ingredients> ingredients) {
        mContext = context;
        mIngredients=ingredients;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_ingredients, parent, false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        Recipes.Ingredients ingredient=mIngredients.get(position);
        Timber.i("ingredient: "+ingredient.getIngredient());
        if ( !TextUtils.isEmpty(ingredient.getIngredient())){
            holder.ingredienText.setText(ingredient.getIngredient());
            holder.measureText.setText(ingredient.getMeasure());
            holder.quantityText.setText(String.valueOf(ingredient.getQuantity()));
        }

    }

    @Override
    public int getItemCount() {
        if (null == mIngredients) return 0;
        Timber.i("Ingredient counts: "+ mIngredients.size());
        return mIngredients.size();
    }


    public class IngredientsViewHolder extends RecyclerView.ViewHolder{
        // ViewHolder contains these member variables
        public  final TextView measureText;
        public final TextView quantityText;
        public final TextView ingredienText;

        // Construct the viewHolder object that accepts the entire item row
        // and does the view lookups. The entire ror views is stored in itemView.
        public IngredientsViewHolder(View itemView) {
            super(itemView);
            this.measureText = itemView.findViewById(R.id.measure);
            this.quantityText = itemView.findViewById(R.id.quantity);
            this.ingredienText = itemView.findViewById(R.id.ingredient);
        }
    }
}
