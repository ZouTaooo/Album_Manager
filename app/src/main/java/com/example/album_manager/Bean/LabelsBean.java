package com.example.album_manager.Bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Labels", strict = false)
public class LabelsBean {
    @Element
    private String Confidence;

    @Element(required = false)
    private String FirstCategory;

    @Element
    private String Name;

    @Element(required = false)
    private String SecondCategory;

    public String getConfidence() {
        return Confidence;
    }

    public void setConfidence(String Confidence) {
        this.Confidence = Confidence;
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
