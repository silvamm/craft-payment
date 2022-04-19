package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.craft.geometry.BoundingBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TextractBoundingBox implements BoundingBox {

    private Float top;
    private Float left;
    private Float height;
    private Float width;

    public TextractBoundingBox(software.amazon.awssdk.services.textract.model.BoundingBox textractBoundingBox){
        this.top = textractBoundingBox.top();
        this.left = textractBoundingBox.left();
        this.height = textractBoundingBox.height();
        this.width = textractBoundingBox.width();
    }
}
