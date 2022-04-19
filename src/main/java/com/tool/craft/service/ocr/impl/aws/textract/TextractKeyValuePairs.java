package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.ocr.KeyValuePairs;
import com.tool.craft.service.ocr.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextractKeyValuePairs implements KeyValuePairs {

    private Text key;
    private Text value;

}
