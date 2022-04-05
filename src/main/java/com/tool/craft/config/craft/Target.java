package com.tool.craft.config.craft;

import com.tool.craft.service.craft.geometry.position.AmountPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Target {

    private String label;
    private AmountPosition amountPosition;

}
