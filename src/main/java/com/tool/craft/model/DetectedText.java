package com.tool.craft.model;

import com.tool.craft.model.interfaces.BoundingBox;
import com.tool.craft.model.interfaces.Text;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

@AllArgsConstructor
public class DetectedText implements Text {

    private String value;
    private BoundingBox boundingBox;

    public DetectedText(TextDetection textDetection){
        this.value = textDetection.detectedText();
        this.boundingBox = new RekognitionBoundingBox(textDetection.geometry().boundingBox());
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public boolean contains(String text){
        return value.toLowerCase().contains(text.toLowerCase());
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
