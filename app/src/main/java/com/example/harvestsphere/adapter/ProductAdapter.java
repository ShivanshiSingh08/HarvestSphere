package com.example.harvestsphere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.harvestsphere.R;
import com.example.harvestsphere.model.CropModel;
import com.example.harvestsphere.model.FertilizerModel;
import com.example.harvestsphere.model.PesticideModel;
import com.example.harvestsphere.model.ToolModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CROP = 0;
    private static final int TYPE_FERTILIZER = 1;
    private static final int TYPE_PESTICIDE = 2;
    private static final int TYPE_TOOL = 3;

    private List<Object> itemList;
    private Context context;

    public ProductAdapter(List<Object> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position) instanceof CropModel) {
            return TYPE_CROP;
        } else if (itemList.get(position) instanceof FertilizerModel) {
            return TYPE_FERTILIZER;
        } else if (itemList.get(position) instanceof PesticideModel) {
            return TYPE_PESTICIDE;
        } else if (itemList.get(position) instanceof ToolModel) {
            return TYPE_TOOL;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_CROP) {
            View view = inflater.inflate(R.layout.item_crop, parent, false);
            return new CropViewHolder(view);
        } else if (viewType == TYPE_FERTILIZER) {
            View view = inflater.inflate(R.layout.item_fertilizer, parent, false);
            return new FertilizerViewHolder(view);
        } else if (viewType == TYPE_PESTICIDE) {
            View view = inflater.inflate(R.layout.item_pesticide, parent, false);
            return new PesticideViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_tool, parent, false);
            return new ToolViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_CROP) {
            CropModel crop = (CropModel) itemList.get(position);
            ((CropViewHolder) holder).bind(crop);
        } else if (viewType == TYPE_FERTILIZER) {
            FertilizerModel fertilizer = (FertilizerModel) itemList.get(position);
            ((FertilizerViewHolder) holder).bind(fertilizer);
        } else if (viewType == TYPE_PESTICIDE) {
            PesticideModel pesticide = (PesticideModel) itemList.get(position);
            ((PesticideViewHolder) holder).bind(pesticide);
        } else if (viewType == TYPE_TOOL) {
            ToolModel tool = (ToolModel) itemList.get(position);
            ((ToolViewHolder) holder).bind(tool);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder classes for each type with Add to Cart functionality
    static class CropViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, seasonTextView, priceTextView, soilTypeTextView, descriptionTextView;
        private ImageView cropImageView;
        private Button addToCartButton;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.crop_name);
            seasonTextView = itemView.findViewById(R.id.crop_season);
            priceTextView = itemView.findViewById(R.id.crop_price);
            soilTypeTextView = itemView.findViewById(R.id.crop_soil_type);
            descriptionTextView = itemView.findViewById(R.id.crop_description);
            cropImageView = itemView.findViewById(R.id.crop_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        void bind(CropModel crop) {
            nameTextView.setText(crop.getName());
            seasonTextView.setText(crop.getSeason());
            priceTextView.setText(String.format("Rs%.2f per kg", crop.getPricePerKg()));
            soilTypeTextView.setText(crop.getSoilType());
            descriptionTextView.setText(crop.getDescription());
            Picasso.get().load(crop.getImage()).into(cropImageView);

            addToCartButton.setOnClickListener(v -> {
                // Add crop to cart logic here
                Toast.makeText(v.getContext(), crop.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });
        }
    }

    static class FertilizerViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, priceTextView, descriptionTextView;
        private ImageView fertilizerImageView;
        private Button addToCartButton;

        public FertilizerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.fertilizer_name);
            priceTextView = itemView.findViewById(R.id.fertilizer_price);
            descriptionTextView = itemView.findViewById(R.id.fertilizer_description);
            fertilizerImageView = itemView.findViewById(R.id.fertilizer_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        void bind(FertilizerModel fertilizer) {
            nameTextView.setText(fertilizer.getName());
            priceTextView.setText(String.format("Rs %.2f per kg", fertilizer.getPricePerKg()));
            descriptionTextView.setText(fertilizer.getDescription());
            Picasso.get().load(fertilizer.getImage()).into(fertilizerImageView);

            addToCartButton.setOnClickListener(v -> {
                // Add fertilizer to cart logic here
                Toast.makeText(v.getContext(), fertilizer.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });
        }
    }

    static class PesticideViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, priceTextView, descriptionTextView;
        private ImageView pesticideImageView;
        private Button addToCartButton;

        public PesticideViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.pesticide_name);
            priceTextView = itemView.findViewById(R.id.pesticide_price);
            descriptionTextView = itemView.findViewById(R.id.pesticide_description);
            pesticideImageView = itemView.findViewById(R.id.pesticide_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        void bind(PesticideModel pesticide) {
            nameTextView.setText(pesticide.getName());
            priceTextView.setText(String.format("Rs %.2f per liter", pesticide.getPricePerLiter()));
            descriptionTextView.setText(pesticide.getDescription());
            Picasso.get().load(pesticide.getImage()).into(pesticideImageView);

            addToCartButton.setOnClickListener(v -> {
                // Add pesticide to cart logic here
                Toast.makeText(v.getContext(), pesticide.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });
        }
    }

    static class ToolViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, priceTextView, descriptionTextView;
        private ImageView toolImageView;
        private Button addToCartButton;

        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tool_name);
            priceTextView = itemView.findViewById(R.id.tool_price);
            descriptionTextView = itemView.findViewById(R.id.tool_description);
            toolImageView = itemView.findViewById(R.id.tool_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        void bind(ToolModel tool) {
            nameTextView.setText(tool.getName());
            priceTextView.setText(String.format("$%.2f", tool.getPrice()));
            descriptionTextView.setText(tool.getDescription());
            Picasso.get().load(tool.getImage()).into(toolImageView);

            addToCartButton.setOnClickListener(v -> {
                // Add tool to cart logic here
                Toast.makeText(v.getContext(), tool.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
