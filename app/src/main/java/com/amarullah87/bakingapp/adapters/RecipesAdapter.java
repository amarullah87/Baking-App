package com.amarullah87.bakingapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amarullah87.bakingapp.R;
import com.amarullah87.bakingapp.services.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 18/08/17.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Recipe> items;
    private ItemClickListener itemClickListener;

    public RecipesAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(Recipe index);
    }

    @Override
    public RecipesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesAdapter.MyViewHolder holder, int position) {
        Recipe item = items.get(position);
        holder.title.setText(item.getName());
        holder.servings.setText(String.valueOf(item.getServings()));

        String imageUrl = item.getImage();
        if(!Objects.equals(imageUrl, "")){
            holder.thumbnail.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse(imageUrl).buildUpon().build();
            Picasso.with(mContext)
                    .load(uri)
                    .placeholder(R.drawable.myicon)
                    .into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return items !=null ? items.size():0;
    }

    public void setRecipeData(ArrayList<Recipe> recipe, Context context){
        items = recipe;
        mContext = context;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.card_view) CardView cardView;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.servings) TextView servings;
        @BindView(R.id.thumbnail) ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemClickListener.onItemClick(items.get(position));
        }
    }
}
