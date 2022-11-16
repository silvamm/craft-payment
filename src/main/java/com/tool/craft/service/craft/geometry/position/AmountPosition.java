package com.tool.craft.service.craft.geometry.position;

import lombok.Getter;

@Getter
public enum AmountPosition {

    TOP (null),
    DOWN (new DownPosition()),
    LEFT(null) ,
    RIGHT(null);

    final StrategyPosition strategy;

    AmountPosition(StrategyPosition strategyPosition) {
        this.strategy = strategyPosition;
    }

}
