package com.tool.craft.domain;

import com.tool.craft.domain.enumm.BillType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bill_type")
    @Enumerated(EnumType.STRING)
    private BillType billType;
    private LocalDateTime date;
    private String description;
    private BigDecimal amount;
    private String receipt;

    @Deprecated
    public Payment() {
    }

    public static Payment newInstance(BillDetails billDetails, String receipt) {
        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setReceipt(receipt);
        payment.setBillType(billDetails.getType());
        payment.setAmount(billDetails.getAmount());
        return payment;
    }


}
