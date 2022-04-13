package com.tool.craft.service.ocr.impl.aws.rekognition;

import com.tool.craft.service.ocr.Text;
import com.tool.craft.service.ocr.TextDetectionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;

@Log4j2
@Service
public class AwsRekognitionService implements TextDetectionService {

    @Override
    public List<Text> findTextsIn(InputStream inputStream) {
        List<TextDetection> textDetections = detectTextLabelsIn(inputStream);
        return textDetections.stream()
                .filter(textDetection -> textDetection.type().equals(TextTypes.LINE))
                .map(RekognitionText::new).collect(toList());
    }

    private List<TextDetection> detectTextLabelsIn(InputStream inputStream) {

        try {
            final RekognitionClient rekognitionClient = RekognitionClient.builder()
                    .region(Region.US_EAST_1)
                    .build();


            SdkBytes sourceBytes = SdkBytes.fromInputStream(inputStream);

            Image image = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectTextRequest textRequest = DetectTextRequest.builder()
                    .image(image)
                    .build();

            DetectTextResponse textResponse = rekognitionClient.detectText(textRequest);
            return textResponse.textDetections();

        } catch (RekognitionException e) {
            log.error(e.getMessage());
        }

        return new ArrayList<>();
    }
}
