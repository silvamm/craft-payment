package com.tool.craft.service;

import com.tool.craft.config.CraftConfig;
import com.tool.craft.enumm.BillType;
import com.tool.craft.model.BillDetails;
import com.tool.craft.model.LabelAmountGeometry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Log4j2
@Service
public class CraftService {

    @Autowired
    private CraftConfig craftConfig;

    public BillDetails findBillDetails(List<TextDetection> textDetections){

        Optional<BillType> optionalBillType = findBillTypeIn(textDetections);
        Optional<String> optionalAmount = findAmountIn(textDetections);

        BillDetails details = new BillDetails();

        optionalBillType.ifPresent(details::setType);
        optionalAmount.ifPresent(a -> {
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(a));
            details.setAmount(amount);
        });

        return details;
    }

    public Optional<BillType> findBillTypeIn(List<TextDetection> textDetections) {
        for (var billsAndRecipients : craftConfig.getBills().entrySet()) {
            for (var recipient : billsAndRecipients.getValue()) {
                if (findRecipient(textDetections, recipient)) {
                    return Optional.ofNullable(billsAndRecipients.getKey());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<String> findAmountIn(List<TextDetection> textDetections) {

        textDetections = onlyLines(textDetections);

        final LabelAmountGeometry labelAmount = new LabelAmountGeometry();

        for (var textDetection : textDetections) {

            if (!labelAmount.found() && (textDetection.detectedText().toLowerCase().contains("total a pagar") ||
                    textDetection.detectedText().toLowerCase().contains("valor"))) {
                labelAmount.set(textDetection.geometry().boundingBox().top(),
                        textDetection.geometry().boundingBox().left());
                continue;
            }

            if (labelAmount.found() &&
                    labelAmount.above(
                            textDetection.geometry().boundingBox().top(),
                            textDetection.geometry().boundingBox().left())) {
                String amount = onlyNumberAndDot(textDetection.detectedText());
                return Optional.ofNullable(amount);
            }

        }
        return Optional.empty();
    }

    private String onlyNumberAndDot(String amount){
        String newAmount;
        newAmount = amount.replaceAll("[^0-9,]","");
        newAmount = amount.replaceAll(",", ".");
        return newAmount;
    }

    private List<TextDetection> onlyLines(List<TextDetection> textDetections){
        return textDetections.stream()
                .filter(t -> t.type().equals(TextTypes.LINE))
                .collect(toList());
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
        return textDetection.detectedText().toLowerCase().contains(recipient);
    }


}
