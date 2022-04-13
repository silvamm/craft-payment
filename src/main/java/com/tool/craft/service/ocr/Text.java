package com.tool.craft.service.ocr;

import com.tool.craft.service.craft.geometry.BoundingBox;

public interface Text {

    String get();

    boolean contains(String text);

    BoundingBox getBoundingBox();
}
