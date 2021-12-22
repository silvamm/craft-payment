package com.tool.craft.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class AwsRekognitionService {

    public List<TextDetection> detectTextLabelsIn(InputStream inputStream) {

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
