package com.example.album_manager.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.album_manager.Adapter.PicAdapter;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.R;

import org.litepal.crud.DataSupport;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    final int THIRD_LEVEL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = getIntent();
        String SecondCategory = intent.getStringExtra("SecondCategory");
        //设置标题栏为二级标签的名字
        setTitle(SecondCategory);
        List<Picture> pictures = DataSupport.where("labelSecondCategory=?", SecondCategory).find(Picture.class);
        recyclerView = findViewById(R.id.gallery_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(GalleryActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        PicAdapter picAdapter = new PicAdapter(pictures, this, THIRD_LEVEL);
        recyclerView.setAdapter(picAdapter);

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
