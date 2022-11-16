package com.tool.craft.util;

public class MoneyUtils {

    public static String onlyNumberAndDot(String amount){
        String newAmount = amount.replaceAll("[^0-9,]","");
        newAmount = newAmount.replaceAll(",", ".");
        return newAmount;
    }
}
