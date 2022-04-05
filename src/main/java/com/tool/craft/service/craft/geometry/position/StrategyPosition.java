package com.tool.craft.service.craft.geometry.position;

import com.tool.craft.service.craft.geometry.BoundingBox;

public interface StrategyPosition {

    boolean match(BoundingBox amountBoundingBox, BoundingBox labelBoundingBox);
}
