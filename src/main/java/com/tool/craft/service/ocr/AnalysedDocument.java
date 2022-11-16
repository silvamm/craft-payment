package com.tool.craft.service.ocr;

import java.util.List;

public interface AnalysedDocument {

    List<Text> getTexts();
    List<LabelAndInputValue> getLabelAndInputValues();

}
