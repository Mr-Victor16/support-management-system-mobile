package com.example.support_management_system_mobile.ui.ticket.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Image;

public class ImagePagerAdapter extends ListAdapter<Image, ImagePagerAdapter.ImageViewHolder> {
    public ImagePagerAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fullscreen_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = getItem(position);

        String base64String = "data:image/jpeg;base64," + image.getContent();

        Glide.with(holder.itemView.getContext())
                .load(base64String)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error_placeholder)
                .into(holder.imageView);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fullscreen_image_view);
        }
    }

    private static final DiffUtil.ItemCallback<Image> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Image oldItem, @NonNull Image newItem) {
            return oldItem.equals(newItem);
        }
    };
}
