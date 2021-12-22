package com.tool.craft.model;

import com.tool.craft.enumm.BillType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDetails {
    private BigDecimal amount;
    private BillType type;

    public boolean filled(){
        return amount != null && type != null;
    }
}
