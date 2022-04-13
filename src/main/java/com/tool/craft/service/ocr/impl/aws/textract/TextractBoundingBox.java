package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.craft.geometry.BoundingBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TextractBoundingBox implements BoundingBox {

    private Float top;
    private Float left;

    public TextractBoundingBox(software.amazon.awssdk.services.textract.model.BoundingBox textractBoundingBox){
        this.top = textractBoundingBox.top();
        this.left = textractBoundingBox.left();
    }
}
