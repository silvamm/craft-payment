package com.tool.craft.model;

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

    public boolean above(float top, float left) {
        final float differenceTop = top - this.top;
        final float differenceLeft = left - this.left;
        return differenceTop < 0.02 && differenceLeft < 0.003005;
    }

    public Float getTop() {
        return top;
    }

    public Float getLeft() {
        return left;
    }
}
