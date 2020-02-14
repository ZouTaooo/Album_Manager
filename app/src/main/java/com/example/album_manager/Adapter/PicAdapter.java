package com.example.album_manager.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.R;

import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {
    private List<Picture> mPicList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView PicImage;
        TextView PicName;

        public ViewHolder(View view) {
            super(view);
            PicImage = view.findViewById(R.id.pic_img);
            PicName = view.findViewById(R.id.pic_title);
        }
    }

    public PicAdapter(List<Picture> PicList,Context context) {
        this.mPicList = PicList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picture pic = mPicList.get(position);
        //加载图片
        Glide.with(context).load(pic.getPath()).into(holder.PicImage);
        holder.PicName.setText(pic.getName());
    }

    @Override
    public int getItemCount() {
        return mPicList.size();
    }
}
