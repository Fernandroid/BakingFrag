package com.example.asus.bakingFrag.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.bakingFrag.R;
import com.example.asus.bakingFrag.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewholder> {

    //Store RecipesAdapter member variables
    private List<Recipes> mRecipesData;
    private Context mContext;
    // Create Adapter member Listner for click on rows in recyclerView
    private OnItemClickListener mListener;

    public RecipesAdapter(Context context) {
        mContext = context;
    }

    // Method to allow the parent activity to set up the OnItemClickListener
    public void setOnItemClickListener(OnItemClickListener listner) {
        mListener = listner;
    }

    @NonNull
    @Override
    public RecipeViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new RecipeViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewholder holder, int position) {
        Timber.i("onBindViewHolder called");
        Recipes currentRecipes = mRecipesData.get(position);
        Timber.i(currentRecipes.getName());
        holder.textView.setText(currentRecipes.getName());
        Log.i("Adapter",currentRecipes.getName());
        if ( !TextUtils.isEmpty(currentRecipes.getImage())) {//currentRecipes.getImage() != null |
            Timber.i("Load image from internet");
            Uri url = Utils.createUrlImage("w300", currentRecipes.getImage());
            Picasso.with(mContext).load(url).into(holder.imageView);
        } else {
            Timber.i("Load image from json");
            loadSavedImage(position,holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mRecipesData) return 0;
        Timber.i("adapter counts: "+ mRecipesData.size());
        return mRecipesData.size();
    }

    public void setRecipesData(List<Recipes> recipesData) {
        mRecipesData = recipesData;
        notifyDataSetChanged();
    }

    public void loadSavedImage(int position, ImageView imageView) {

        switch (position) {
            case 0: imageView.setImageResource(R.drawable.nutella_pie);
                Timber.i("load image: Nutella Pie");
                break;
            case 1: imageView.setImageResource(R.drawable.brownies);
                Timber.i("load image: Brownies");
                break;
            case 2: imageView.setImageResource(R.drawable.yellow_cake);
                Timber.i("load image: Yellow cake");
                break;
            case 3: imageView.setImageResource(R.drawable.cheese_cake);
                Timber.i("load image: Cheese cake");
                break;
            default:
                Timber.w("Nothing image available");
        }
    }

    // Create OnItemClickLisetner interface to trigger onItemClick
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    /**
     * Class to cache the children views of a Recipes list item for recyclerVire.
     */
    public class RecipeViewholder extends RecyclerView.ViewHolder {
        // ViewHolder contains these member variables
        private   final ImageView imageView;
        private final TextView textView;

        // Construct the viewHolder object that accepts the entire item row
        // and does the view lookups. The entire ror views is stored in itemView.
        public RecipeViewholder(final View itemView) {
            super(itemView);
            //imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.tv_name);
            imageView=itemView.findViewById(R.id.iv_sweets);
            //Set up the onClickListner to entire row views
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mListener != null) {
                        // Get item position
                        int position = getAdapterPosition();
                        //Trigger onItemClick to the adapter in the parent acitivity
                        mListener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }


}
