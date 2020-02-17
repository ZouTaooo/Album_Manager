package com.example.album_manager.Bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Labels", strict = false)
public class LabelsBean {
    @Element
    private int Confidence;

    @Element(required = false)
    private String FirstCategory;

    @Element
    private String Name;

    @Element(required = false)
    private String SecondCategory;

    public int getConfidence() {
        return Confidence;
    }

    public void setConfidence(int confidence) {
        Confidence = confidence;
    }

    public String getFirstCategory() {
        return FirstCategory;
    }

    public void setFirstCategory(String FirstCategory) {
        this.FirstCategory = FirstCategory;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSecondCategory() {
        return SecondCategory;
    }

    public void setSecondCategory(String SecondCategory) {
        this.SecondCategory = SecondCategory;
    }
}
