package com.tool.craft.service.craft;

import com.tool.craft.config.CraftConfig;
import com.tool.craft.model.enumm.BillType;
import com.tool.craft.model.BillDetails;
import com.tool.craft.service.craft.geometry.LabelAmountGeometry;
import com.tool.craft.service.ocr.LabelAndInputValue;
import com.tool.craft.service.ocr.Text;
import com.tool.craft.service.ocr.AnalysedDocument;
import com.tool.craft.util.MoneyUtils;
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
    
    public Optional<BillDetails> findBillDetailsIn(AnalysedDocument textsAndKeyValuePairs){
        log.info("Iniciando busca dos detalhes da conta");
        Optional<BillType> optionalBillType = findBillTypeIn(textsAndKeyValuePairs.getTexts());
        Optional<String> optionalAmount = findAmountIn(textsAndKeyValuePairs.getLabelAndInputValues());

        if(optionalBillType.isPresent() && optionalAmount.isPresent()){
            log.info("Detalhes da conta encontrado");
            return Optional.of(new BillDetails(optionalBillType.get(), optionalAmount.get()));
        }
        log.info("Detalhes da conta não encontrado");
        return Optional.empty();
    }

    public Optional<String> findAmountIn(List<LabelAndInputValue> keyValues){

        for(var targetLabel : craftConfig.getLabelTargets()) {
            for (var keyValue : keyValues) {
                if (keyValue.getLabel().contains(targetLabel)) {
                    String amount = MoneyUtils.onlyNumberAndDot(keyValue.getInputValue().get());
                    return Optional.of(amount);
                }
            }
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


    public Optional<String> forceFindAmountIn(List<Text> texts) {

        final LabelAmountGeometry labelAmount = new LabelAmountGeometry();

        for (var text : texts) {
            if (!labelAmount.hasLabel()) {
                craftConfig.getLabelTargets().stream()
                        .filter(text::contains)
                        .findAny()
                        .ifPresent(target -> labelAmount.setBoundingBox(text.getBoundingBox()));
                continue;
            }
            // aqui vou ter que verificar se existe algum valor proximo que seja numerico, com ponto e duas casas decimais
            if (labelAmount.hasLabel() && labelAmount.labelFor(text.getBoundingBox())){
                String amount = MoneyUtils.onlyNumberAndDot(text.get());
                return Optional.of(amount);
            }
        }

        return Optional.empty();
    }



}
