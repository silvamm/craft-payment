package com.tool.craft.service.craft.geometry;

import com.tool.craft.service.craft.geometry.position.AmountPosition;

public class LabelAmountGeometry {

    private BoundingBox label;
    private AmountPosition amountPosition;

    public void setAmountPosition(AmountPosition amountPosition) {
        this.amountPosition = amountPosition;
    }

    public boolean labelFor(BoundingBox boundingBox){
        return amountPosition.getStrategy().match(boundingBox, this.label);
    }

    public void setLabel(BoundingBox label){
        this.label = label;
    }

    public boolean hasLabel() {
        return label != null;
    }

}
