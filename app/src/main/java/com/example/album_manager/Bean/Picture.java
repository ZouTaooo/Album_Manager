package com.example.album_manager.Bean;

import org.litepal.crud.DataSupport;


public class Picture extends DataSupport {
    //long id;//id

    String name;//

    String path;//路径

    boolean isPut;//是否上传

    String labelName;//标签名

    String labelFirstCategory;

    String labelSecondCategory;

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPut() {
        return isPut;
    }

    public void setPut(boolean put) {
        isPut = put;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelFirstCategory() {
        return labelFirstCategory;
    }

    public void setLabelFirstCategory(String labelFirstCategory) {
        this.labelFirstCategory = labelFirstCategory;
    }

    public String getLabelSecondCategory() {
        return labelSecondCategory;
    }

    public void setLabelSecondCategory(String labelSecondCategory) {
        this.labelSecondCategory = labelSecondCategory;
    }
}
