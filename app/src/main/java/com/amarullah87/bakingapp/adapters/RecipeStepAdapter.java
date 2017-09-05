package com.amarullah87.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amarullah87.bakingapp.R;
import com.amarullah87.bakingapp.services.Recipe;
import com.amarullah87.bakingapp.services.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 24/08/17.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.MyViewHolder> {

    List<Step> stepList;
    private String recipeName;
    private ItemClickListener itemClickListener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_step_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Step step = stepList.get(position);
        holder.stepTitle.setText(step.getId() + ". " + step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        return stepList != null ? stepList.size(): 0;
    }

    public interface ItemClickListener{
        void onItemClick(List<Step> steps, int index, String recipeName);
    }

    public RecipeStepAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setRecipeDetail(List<Recipe> recipes, Context context){
        stepList = recipes.get(0).getSteps();
        recipeName = recipes.get(0).getName();

        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_title) TextView stepTitle;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.onItemClick(stepList, position, recipeName);
        }
    }
}
