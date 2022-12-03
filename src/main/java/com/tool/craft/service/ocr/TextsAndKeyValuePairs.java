package com.tool.craft.service.ocr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextsAndKeyValuePairs {

    private List<Text> texts = new ArrayList<>();
    private List<LabelAndInputValue> labelAndInputValues = new ArrayList<>();

}
