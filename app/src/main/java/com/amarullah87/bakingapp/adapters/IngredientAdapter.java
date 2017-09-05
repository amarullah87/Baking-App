package com.amarullah87.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amarullah87.bakingapp.R;
import com.amarullah87.bakingapp.services.Ingredient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 24/08/17.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {

    private Context mContext;
    private List<Ingredient> items;

    public IngredientAdapter(Context mContext, List<Ingredient> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ingredient_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ingredient item = items.get(position);
        holder.ingredientName.setText(item.getIngredient());
        holder.ingredientQty.setText(String.valueOf(item.getQuantity()) + " " + item.getMeasure());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_name) TextView ingredientName;
        @BindView(R.id.ingredient_qty) TextView ingredientQty;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
