package com.tool.craft.service.ocr;

import com.tool.craft.service.craft.geometry.BoundingBox;

public interface Text {

    String get();

    default boolean contains(String text){
        if (get() == null || text == null) return false;
        return get().toLowerCase().trim().contains(text.toLowerCase().trim());
    }

    BoundingBox getBoundingBox();
}
