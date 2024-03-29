package com.tool.craft.entity;

import com.tool.craft.enumm.BillType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
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


}
