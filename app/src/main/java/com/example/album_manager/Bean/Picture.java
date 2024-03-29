package com.example.album_manager.Bean;

import org.litepal.crud.DataSupport;


public class Picture extends DataSupport {

    int id;//id

    String name;//title

    String path;//路径

    boolean isPut;//是否上传

    String labelName;//标签名

    int Confidence;//可信度

    String labelFirstCategory;

    String labelSecondCategory;

    public int getConfidence() {
        return Confidence;
    }

    public void setConfidence(int confidence) {
        Confidence = confidence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
