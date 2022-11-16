package com.tool.craft.service.craft.geometry;

import com.tool.craft.service.craft.geometry.position.DownPosition;

public class LabelAmountGeometry {

    private BoundingBox boundingBox;

    public boolean labelFor(BoundingBox boundingBox){
        return new DownPosition().match(boundingBox, this.boundingBox);
    }

    public void setBoundingBox(BoundingBox boundingBox){
        this.boundingBox = boundingBox;
    }

    public boolean hasLabel() {
        return boundingBox != null;
    }

}
