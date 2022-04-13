package com.tool.craft.service.ocr.impl.aws.rekognition;

import com.tool.craft.service.craft.geometry.BoundingBox;
import com.tool.craft.service.ocr.Text;
import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

@AllArgsConstructor
public class RekognitionText implements Text {

    private String value;
    private BoundingBox boundingBox;

    public RekognitionText(TextDetection textDetection){
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

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

}
