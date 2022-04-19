package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.craft.geometry.BoundingBox;
import com.tool.craft.service.ocr.Text;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.services.textract.model.Block;

@ToString
public class TextractText implements Text {

    private final String value;
    @Setter
    private BoundingBox boundingBox;

    public TextractText(Block block) {
        this.value = block.text();
        this.boundingBox = new TextractBoundingBox(block.geometry().boundingBox());
    }

    public TextractText(String value){
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public boolean contains(String text) {
        return this.value.toLowerCase().trim().contains(text.toLowerCase().trim());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
