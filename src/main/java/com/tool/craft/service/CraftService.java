package com.tool.craft.service;

import com.tool.craft.config.CraftConfig;
import com.tool.craft.enumm.BillType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.InputStream;
import java.util.*;

@Log4j2
@Service
public class CraftService {

    @Autowired
    private CraftConfig craftConfig;

    @Autowired
    private AwsService awsService;

    public Optional<BillType> startSearchIn(InputStream inputStream) {
        List<TextDetection> textDetections = awsService.detectTextLabelsIn(inputStream);
        return findBillType(textDetections);
    }

    private Optional<BillType> findBillType(List<TextDetection> textDetections) {
        for (var billsAndRecipients : craftConfig.getBills().entrySet()) {
            for (var recipient : billsAndRecipients.getValue()) {
                if (findRecipient(textDetections, recipient)) {
                    return Optional.ofNullable(billsAndRecipients.getKey());
                }
            }
        }
        return Optional.empty();
    }

    private boolean findRecipient(List<TextDetection> textDetections, String recipient) {
        for (TextDetection textDetection : textDetections) {
            if (containsRecipient(textDetection, recipient)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsRecipient(TextDetection textDetection, String recipient) {
        return textDetection.type().equals(TextTypes.LINE)
                && textDetection.detectedText().toLowerCase().contains(recipient);
    }


}
