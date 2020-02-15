package com.example.album_manager.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.album_manager.Activity.GalleryActivity;
import com.example.album_manager.Activity.InfoActivity;
import com.example.album_manager.Activity.SecondCategoryActivity;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.R;

import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {
    private List<Picture> mPicList;
    private int level;
    private Context context;
    private static final String TAG = "PicAdapter";
    private final int FIRST_LEVEL = 1;
    private final int SECOND_LEVEL = 2;
    private final int THIRD_LEVEL = 3;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView PicImage;
        TextView PicName;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            PicImage = view.findViewById(R.id.pic_img);
            PicName = view.findViewById(R.id.pic_title);
        }
    }

    public PicAdapter(List<Picture> PicList, Context context, int level) {
        this.level = level;
        this.mPicList = PicList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (level) {
                    case FIRST_LEVEL:
                        intent = new Intent(context, SecondCategoryActivity.class);
                        intent.putExtra("FirstCategory", holder.PicName.getText());
                        Log.e(TAG, "onClick: 传递的一级类别是：" + holder.PicName.getText());
                        context.startActivity(intent);
                        break;
                    case SECOND_LEVEL:
                        intent = new Intent(context, GalleryActivity.class);
                        intent.putExtra("SecondCategory", holder.PicName.getText());
                        Log.e(TAG, "onClick: 传递的二级类别是：" + holder.PicName.getText());
                        context.startActivity(intent);
                        break;
                    case THIRD_LEVEL:
                        intent = new Intent(context, InfoActivity.class);
                        intent.putExtra("PicName", holder.PicName.getText());
                        Log.e(TAG, "onClick: 传递的图片名是：" + holder.PicName.getText());
                        context.startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
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
