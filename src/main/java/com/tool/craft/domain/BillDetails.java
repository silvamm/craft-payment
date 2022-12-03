package com.tool.craft.domain;

import com.tool.craft.domain.enumm.BillType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BillDetails {

    private BillType type;
    private BigDecimal amount;

    public BillDetails(BillType type, String amount){
        this.type = type;
        this.amount = new BigDecimal(amount);
    }
}
