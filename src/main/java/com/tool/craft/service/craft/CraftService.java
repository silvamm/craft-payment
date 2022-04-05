package com.tool.craft.service.craft;

import com.tool.craft.config.craft.CraftConfig;
import com.tool.craft.enumm.BillType;
import com.tool.craft.entity.BillDetails;
import com.tool.craft.service.craft.text.Text;
import com.tool.craft.service.craft.geometry.LabelAmountGeometry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CraftService {

    private final CraftConfig craftConfig;
    private List<String> labelsToFind;

    public Optional<BillDetails> findBillDetailsIn(List<Text> texts){

        Optional<BillType> optionalBillType = findBillTypeIn(texts);
        Optional<String> optionalAmount = findAmountIn(texts);

        if(optionalBillType.isPresent() && optionalAmount.isPresent()){
            return Optional.of(new BillDetails(optionalBillType.get(), optionalAmount.get()));
        }

        return Optional.empty();
    }

    public Optional<BillType> findBillTypeIn(List<Text> texts) {
        for (var billsAndRecipients : craftConfig.getBills().entrySet()) {
            for (var recipient : billsAndRecipients.getValue()) {
                if (findRecipientIn(texts, recipient)) {
                    return Optional.ofNullable(billsAndRecipients.getKey());
                }
            }
        }
        return Optional.empty();
    }

    private boolean findRecipientIn(List<Text> texts, String recipient) {
        for (Text text : texts) {
            if (text.contains(recipient)) return true;
        }
        return false;
    }

    public Optional<String> findAmountIn(List<Text> texts) {

        final LabelAmountGeometry labelAmount = new LabelAmountGeometry();

        for (var text : texts) {
            if (!labelAmount.hasLabel()) {
                craftConfig.getTargets().stream()
                        .filter(target -> text.contains(target.getLabel()))
                        .findAny()
                        .ifPresent(target -> {
                            labelAmount.setLabel(text.getBoundingBox());
                            labelAmount.setAmountPosition(target.getAmountPosition());
                        });
                continue;
            }

            if (labelAmount.hasLabel() && labelAmount.labelFor(text.getBoundingBox())){
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
