package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.craft.geometry.BoundingBox;
import com.tool.craft.service.ocr.Text;
import software.amazon.awssdk.services.textract.model.Block;

public class TextractText implements Text {

    private final String text;
    private final BoundingBox boundingBox;

    public TextractText(Block block) {
        this.text = block.text();
        this.boundingBox = new TextractBoundingBox(block.geometry().boundingBox());
    }

    @Override
    public String get() {
        return text;
    }

    @Override
    public boolean contains(String text) {
        return this.text.toLowerCase().trim().contains(text.toLowerCase().trim());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
