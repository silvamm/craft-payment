package com.tool.craft.service.ocr.impl.aws.rekognition;

import com.tool.craft.service.craft.geometry.BoundingBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RekognitionBoundingBox implements BoundingBox {

    private Float top;
    private Float left;

    public RekognitionBoundingBox(software.amazon.awssdk.services.rekognition.model.BoundingBox boundingBoxRekognition){
        this.top = boundingBoxRekognition.top();
        this.left = boundingBoxRekognition.left();
    }
}
