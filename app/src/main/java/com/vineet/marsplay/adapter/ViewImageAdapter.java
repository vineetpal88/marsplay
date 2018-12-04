package com.vineet.marsplay.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vineet.marsplay.R;
import com.vineet.marsplay.callback.OnRecyclerItemClickCalback;
import com.vineet.marsplay.event.ImageModel;
import com.vineet.marsplay.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ViewImageAdapter extends RecyclerView.Adapter<ViewImageHolder> {

    private ArrayList<ImageModel> imageList = new ArrayList<>();
    private Context c;
    OnRecyclerItemClickCalback listner;

    public ViewImageAdapter(Context c, OnRecyclerItemClickCalback listner) {
        this.c = c;
        this.listner = listner;
    }

    /*
    INITIALIZE VIEWHOLDER
     */

    @Override
    public ViewImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.row_viewimage, parent, false);
        return new ViewImageHolder(v);
    }

    /*
    BIND
     */
    @Override
    public void onBindViewHolder(ViewImageHolder holder, int position) {
        String s = imageList.get(position).getImageUrl();

        if (s != null && !s.isEmpty()) {
            String originalImageUrlThumbnail = Utility.getInstance(c).getOriginalImageUrlThumbnail(s);
            Glide.with(c)
                    .load(Uri.parse(originalImageUrlThumbnail))
                    .placeholder(R.drawable.gallery)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.gallery);
        }

        holder.setOnRecyclerItemClickListener(listner);
    }

    public void addAll(List<ImageModel> imageList) {
        this.imageList.addAll(imageList);
        notifyDataSetChanged();
    }

    /*
    TOTAL IMAGE NUM
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public ImageModel getItem(int position) {
        return imageList != null ? imageList.get(position) : null;
    }
}
