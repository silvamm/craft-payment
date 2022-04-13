package com.tool.craft.service.ocr;

import java.io.InputStream;

public interface TextAndKeyValuePairsService {

    TextsAndKeyValuePairs findTextsAndKeyValuePairsIn(InputStream inputStream);
}
