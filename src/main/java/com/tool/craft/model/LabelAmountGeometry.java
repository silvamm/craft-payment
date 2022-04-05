package com.tool.craft.model;

import com.tool.craft.model.interfaces.BoundingBox;

public class LabelAmountGeometry {

    private float top = 0f;
    private float left = 0f;

    public void set(Float topLabelAmount, Float leftLabelAmount) {
        this.left = leftLabelAmount;
        this.top = topLabelAmount;
    }

    public boolean found() {
        return top > 0f && left > 0f;
    }

    public boolean above(BoundingBox boundingBox){
        final float differenceTop = boundingBox.getTop() - this.top;
        final float differenceLeft = boundingBox.getLeft() - this.left;
        return differenceTop < 0.02 && differenceLeft < 0.003005;
    }

}
