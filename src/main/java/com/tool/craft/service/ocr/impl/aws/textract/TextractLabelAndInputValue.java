package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.ocr.LabelAndInputValue;
import com.tool.craft.service.ocr.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextractLabelAndInputValue implements LabelAndInputValue {

    private Text label;
    private Text inputValue;

}
