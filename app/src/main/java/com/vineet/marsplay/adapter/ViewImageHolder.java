package com.vineet.marsplay.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vineet.marsplay.R;
import com.vineet.marsplay.callback.OnRecyclerItemClickCalback;

public class ViewImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;
    public RelativeLayout rl_row;
    private OnRecyclerItemClickCalback onRecyclerItemClick;

    public ViewImageHolder(View itemView) {
        super(itemView);
        rl_row = (RelativeLayout) itemView.findViewById(R.id.rl_row);
        imageView = (ImageView) itemView.findViewById(R.id.iv_image);

        rl_row.setOnClickListener(this);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickCalback onRecyclerItemClick) {
        this.onRecyclerItemClick = onRecyclerItemClick;
    }

    @Override
    public void onClick(View view) {
        onRecyclerItemClick.onItemClick(view, getAdapterPosition());
    }
}
