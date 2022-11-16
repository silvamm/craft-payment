package com.tool.craft.service.ocr.impl.aws.textract;

import com.tool.craft.service.ocr.LabelAndInputValue;
import com.tool.craft.service.ocr.Text;
import com.tool.craft.service.ocr.AnalysedDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TextractTextsAndKeyValuePairs implements AnalysedDocument {

    private List<Text> texts = new ArrayList<>();
    private List<LabelAndInputValue> labelAndInputValues = new ArrayList<>();

}
