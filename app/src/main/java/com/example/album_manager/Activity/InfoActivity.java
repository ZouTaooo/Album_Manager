package com.example.album_manager.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.R;

import org.litepal.crud.DataSupport;

public class InfoActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        String PicName = intent.getStringExtra("PicName");
        Picture picture =  DataSupport.where("name=?", PicName).findFirst(Picture.class);
        //设置标题栏为当前图片的标签名
        setTitle(picture.getLabelName());
        //显示图片
        imageView = findViewById(R.id.image);
        Glide.with(InfoActivity.this).load(picture.getPath()).into(imageView);

        setBackButton();
    }

    private void setBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
