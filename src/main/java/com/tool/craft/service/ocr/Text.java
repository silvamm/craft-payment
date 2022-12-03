package com.tool.craft.service.ocr;


import lombok.ToString;

@ToString
public class Text {

    private final String value;

    public Text(String value){
        this.value = value;
    }

    public boolean contains(String text){
        if (get() == null || text == null) return false;
        return get().toLowerCase().trim().contains(text.toLowerCase().trim());
    }

    public String get() {
        return value;
    }

}
