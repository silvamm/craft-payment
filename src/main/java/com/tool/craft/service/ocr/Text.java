package com.tool.craft.service.ocr;


import lombok.ToString;
import software.amazon.awssdk.services.textract.model.Block;

@ToString
public class Text {

    private final String value;

    public Text(Block block) {
        this.value = block.text();

    }

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
