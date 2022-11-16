package com.tool.craft.service.ocr;

import java.io.InputStream;

public interface AnalyzeDocumentService {

    AnalysedDocument analyseDocumentoFrom(InputStream inputStream);
}
