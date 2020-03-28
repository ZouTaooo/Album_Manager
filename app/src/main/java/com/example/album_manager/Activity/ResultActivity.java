package com.example.album_manager.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.album_manager.R;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    private TextView m_res;
    private ImageView m_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        String res = intent.getStringExtra("res");
        initView(res, path);
    }

    private void initView(String res, String path) {
        m_res = findViewById(R.id.res);
        m_res.setText(res);
        m_img = findViewById(R.id.Img);
        Glide.with(ResultActivity.this).load(path).into(m_img);
        setBackButton();
        setTitle("分析结果");
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
