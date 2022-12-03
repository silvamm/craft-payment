package com.tool.craft.service.ocr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LabelAndInputValue {

    private Text labelText;
    private Text inputText;
    public LabelAndInputValue(String label, String input) {
        this.labelText = new Text(label);
        this.inputText = new Text(input);
    }

    public String getLabelValue(){
        return labelText.get();
    }

    public String getInputValue(){
        return inputText.get();
    }

}
