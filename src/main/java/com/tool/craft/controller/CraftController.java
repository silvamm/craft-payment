package com.tool.craft.controller;

import com.tool.craft.config.CraftConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CraftController {

    @Autowired
    private CraftConfig craftConfig;

    @GetMapping("/start")
    public void start(){
        List<TextDetection> textDetections = detectTextLabels();

        for (TextDetection textDetection: textDetections){

            for (Map.Entry<String, List<String>> entry : craftConfig.getContas().entrySet()) {
                List<String> beneficiarios = entry.getValue();
                String conta = entry.getKey();
                for (String beneficiario : beneficiarios) {

                    if(textDetection.type().equals(TextTypes.LINE)
                            && textDetection.detectedText().toLowerCase().contains(beneficiario)){
                        System.out.println("Encontrei conta de " + conta);
                        break;
                    }

                }

            }

        }

        System.out.println("Finalizado");
    }

    public List<TextDetection> detectTextLabels() {

        try {

            RekognitionClient rekognitionClient = RekognitionClient.builder()
                    .region(Region.US_WEST_2)
                    .build();

            InputStream sourceStream = new FileInputStream("/");
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            // Create an Image object for the source image
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectTextRequest textRequest = DetectTextRequest.builder()
                    .image(souImage)
                    .build();

            DetectTextResponse textResponse = rekognitionClient.detectText(textRequest);
            return textResponse.textDetections();

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }
}
