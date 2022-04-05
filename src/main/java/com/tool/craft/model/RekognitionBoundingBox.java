package com.tool.craft.model;

import com.tool.craft.model.interfaces.BoundingBox;
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
