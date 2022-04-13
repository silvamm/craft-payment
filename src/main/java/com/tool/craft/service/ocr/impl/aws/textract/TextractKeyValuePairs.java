package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.ocr.KeyValuePairs;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TextractKeyValuePairs implements KeyValuePairs {

    private String key;
    private String value;

    public boolean containsKey(String key){
        return this.key.toLowerCase().trim().contains(key.toLowerCase().trim());
    }

}
