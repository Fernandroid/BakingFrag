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

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsHolder> {

    private Context mContext;
    private List<Recipes.Steps> mStepsList;
    // Create Adapter member Listner for click on rows in recyclerView
    private OnStepClickListener mListener;
    public StepsAdapter(Context c, List<Recipes.Steps> stepsList) {
        mContext=c;
        mStepsList=stepsList;
        notifyDataSetChanged();
    }
    // Create OnItemClickLisetner interface to trigger onItemClick
    public interface  OnStepClickListener{
        void onStepClickListener(View itemView,int position);
    }

    // Method to allow the parent activity to set up the OnItemClickListener
    public void setOnStepClickListener(OnStepClickListener listener) {
        mListener = listener;
    }
    @NonNull
    @Override
    public StepsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_steps, parent, false);
        return new StepsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsHolder holder, int position) {
        Recipes.Steps step=mStepsList.get(position);
        Timber.i("ingredient: "+step.getShortDescription());
        if ( !TextUtils.isEmpty(step.getShortDescription())|step.getShortDescription()!=""){
            holder.shortDescr.setText(step.getShortDescription());
            holder.number.setText(String.valueOf(position+1));
        }
    }

    @Override
    public int getItemCount() {
        if (null == mStepsList) return 0;
        Timber.i("Ingredient counts: "+ mStepsList.size());
        return mStepsList.size();
    }

    public class StepsHolder extends RecyclerView.ViewHolder{
        final  TextView number;
        final TextView shortDescr;

        public StepsHolder(final View itemView) {
            super(itemView);
            this.number = itemView.findViewById(R.id.step_number);
            this.shortDescr =  itemView.findViewById(R.id.short_descr);
            //Set up the onClickListner to entire row views
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onStepClickListener(itemView,position);
                }
            });
        }
    }
}
