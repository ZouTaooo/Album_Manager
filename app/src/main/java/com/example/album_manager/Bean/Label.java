package com.example.album_manager.Bean;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "RecognitionResult",strict = false)
public class Label {

    @ElementList(name = "Labels", inline = true)
    private List<LabelsBean> LabelList;

    public List<LabelsBean> getLabelList() {
        return LabelList;
    }

    public void setLabelList(List<LabelsBean> labelList) {
        LabelList = labelList;
    }
}



