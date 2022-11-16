package com.tool.craft.service.ocr;

import java.io.InputStream;
import java.util.List;

public interface TextDetectionService {

    List<Text> findTextsIn(InputStream inputStream);

}
