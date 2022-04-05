package com.tool.craft.service.craft.geometry.position;

import com.tool.craft.service.craft.geometry.BoundingBox;

public class DownPosition implements StrategyPosition {

    @Override
    public boolean match(BoundingBox amountBoundingBox, BoundingBox labelBoundingBox) {
        final float differenceTop = amountBoundingBox.getTop() - labelBoundingBox.getTop();
        final float differenceLeft = amountBoundingBox.getLeft() - labelBoundingBox.getLeft();
        return differenceTop < 0.02 && differenceLeft < 0.003005;
    }
}
