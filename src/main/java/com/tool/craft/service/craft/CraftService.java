package com.tool.craft.service.craft;

import com.tool.craft.config.CraftConfig;
import com.tool.craft.model.enumm.BillType;
import com.tool.craft.model.BillDetails;
import com.tool.craft.service.ocr.LabelAndInputValue;
import com.tool.craft.service.ocr.Text;
import com.tool.craft.service.ocr.TextsAndKeyValuePairs;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.tool.craft.util.MoneyUtils.onlyNumberAndDot;

@Log4j2
@Service
@RequiredArgsConstructor
public class CraftService {

    private final CraftConfig craftConfig;
    
    public Optional<BillDetails> findBillDetailsIn(TextsAndKeyValuePairs textsAndKeyValuePairs){
        log.info("Iniciando busca dos detalhes da conta");
        var optionalBillType = findBillTypeIn(textsAndKeyValuePairs.getTexts());
        var optionalAmount = findAmountIn(textsAndKeyValuePairs.getLabelAndInputValues());

        if(optionalBillType.isPresent() && optionalAmount.isPresent()){
            log.info("Detalhes da conta encontrado");
            var amount = onlyNumberAndDot(optionalAmount.get());
            return Optional.of(new BillDetails(optionalBillType.get(), amount));
        }
        log.info("Detalhes da conta não encontrado");
        return Optional.empty();
    }

    public Optional<String> findAmountIn(List<LabelAndInputValue> inputs) {
        for(var targetLabel : craftConfig.getTargetLabels()){
            for(var input: inputs){
                if(input.getLabelText().contains(targetLabel))
                    return Optional.of(input.getInputValue());
            }
        }
        return Optional.empty();
    }

    public Optional<BillType> findBillTypeIn(List<Text> texts) {
        for (var billsAndRecipients : craftConfig.getBills().entrySet()) {
            for (var recipient : billsAndRecipients.getValue()) {
                if (findRecipientIn(texts, recipient))
                    return Optional.ofNullable(billsAndRecipients.getKey());
            }
        }
        return Optional.empty();
    }

    private boolean findRecipientIn(List<Text> texts, String recipient) {
        return texts.stream().anyMatch(text -> text.contains(recipient));
    }

}
