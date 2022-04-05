package com.tool.craft.service;

import com.tool.craft.config.CraftConfig;
import com.tool.craft.enumm.BillType;
import com.tool.craft.model.DetectedText;
import com.tool.craft.model.entity.BillDetails;
import com.tool.craft.model.LabelAmountGeometry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CraftService {

    @Autowired
    private CraftConfig craftConfig;

    public Optional<BillDetails> findBillDetailsIn(List<DetectedText> texts){

        Optional<BillType> optionalBillType = findBillTypeIn(texts);
        Optional<String> optionalAmount = findAmountIn(texts);

        if(optionalBillType.isPresent() && optionalAmount.isPresent()){
            return Optional.of(new BillDetails(optionalBillType.get(), optionalAmount.get()));
        }

        return Optional.empty();
    }

    public Optional<BillType> findBillTypeIn(List<DetectedText> texts) {
        for (var billsAndRecipients : craftConfig.getBills().entrySet()) {
            for (var recipient : billsAndRecipients.getValue()) {
                if (findRecipientIn(texts, recipient)) {
                    return Optional.ofNullable(billsAndRecipients.getKey());
                }
            }
        }
        return Optional.empty();
    }

    private boolean findRecipientIn(List<DetectedText> texts, String recipient) {
        for (DetectedText text : texts) {
            if (text.contains(recipient)) return true;
        }
        return false;
    }

    public Optional<String> findAmountIn(List<DetectedText> texts) {

        final LabelAmountGeometry labelAmount = new LabelAmountGeometry();

        for (var text : texts) {
            if (!labelAmount.found() && (text.contains("total a pagar") || text.contains("valor"))) {
                labelAmount.set(text.getBoundingBox().getTop(),
                        text.getBoundingBox().getLeft());
                continue;
            }

            if (labelAmount.found() && labelAmount.above(text.getBoundingBox())){
                String amount = onlyNumberAndDot(text.get());
                return Optional.of(amount);
            }
        }

        return Optional.empty();
    }

    private String onlyNumberAndDot(String amount){
        String newAmount = amount.replaceAll("[^0-9,]","");
        newAmount = newAmount.replaceAll(",", ".");
        return newAmount;
    }

}
