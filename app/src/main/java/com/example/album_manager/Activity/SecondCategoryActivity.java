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

import java.util.ArrayList;
import java.util.List;

public class SecondCategoryActivity extends AppCompatActivity {

    final int SECOND_LEVEL = 2;
    final private String[] spot = {"办公场所", "工业", "公共设施", "建筑", "生活/娱乐场所", "自然风光", "其他"};
    final private String[] alive = {"哺乳动物", "宠物", "海洋生物", "花草", "昆虫", "鸟类", "爬行动物", "鱼类", "两栖动物", "树", "其他"};
    final private String[] card = {"表格图表", "单据票据", "卡证", "印刷品", "其他"};
    final private String[] food = {"菜品", "干果坚果", "食材配料", "蔬菜", "水果", "甜品零食", "饮品", "主食", "其他"};
    final private String[] people = {"人像", "人体部位", "其他"};
    final private String[] event = {"庆祝活动", "休闲娱乐活动", "运动", "其他"};
    final private String[] stuff = {"餐具厨具", "穿着饰品", "电子产品及配件", "工具", "家具家装", "家用电器", "其他",
            "交通工具", "警用器材", "乐器", "日常用品", "玩具", "文具办公", "武器", "医疗用品", "艺术品工艺品",
            "运动器械与用品", "标牌标识", "机械装备或车辆"};
    final private String[] other = {"二维码条形码", "屏幕/界面/截图", "违规", "绘画卡通", "其他"};

    private String[] temp = null;

    String FirstCategory;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_category);

        //获取当前是哪个一级目录下
        initFirstCategory();
        //初始化列表
        initRecyclerView();

        setBackButton();
    }

    private void initRecyclerView() {
        List<Picture> pictures = new ArrayList<>();
        for (String string : temp) {
            Picture picture = DataSupport.where("labelFirstCategory=? and labelSecondCategory=? and Confidence>15", FirstCategory, string).findFirst(Picture.class);
            if (picture != null) {
                picture.setName(string);
                pictures.add(picture);
            }
        }
        recyclerView = findViewById(R.id.second_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SecondCategoryActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        PicAdapter picAdapter = new PicAdapter(pictures, this, SECOND_LEVEL,FirstCategory,null);
        recyclerView.setAdapter(picAdapter);
    }

    private void initFirstCategory() {
        Intent intent = getIntent();
        FirstCategory = intent.getStringExtra("FirstCategory");
        //设置标题栏为一级标签的名字
        setTitle(FirstCategory);
        switch (FirstCategory) {
            case "场景":
                temp = spot;
                break;
            case "动植物":
                temp = alive;
                break;
            case "卡证文档":
                temp = card;
                break;
            case "美食":
                temp = food;
                break;
            case "人物":
                temp = people;
                break;
            case "事件":
                temp = event;
                break;
            case "物品":
                temp = stuff;
                break;
            case "其他":
                temp = other;
                break;
            default:
                break;
        }
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
