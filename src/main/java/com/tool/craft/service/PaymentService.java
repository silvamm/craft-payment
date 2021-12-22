package com.tool.craft.service;

import com.tool.craft.enumm.BillType;
import com.tool.craft.model.BillDetails;
import com.tool.craft.model.Payment;
import com.tool.craft.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public void save(BillDetails details, String s3Receipt, InputStream inputStream){
        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setBillType(details.getType());
        payment.setReceipt(s3Receipt);
        payment.setAmount(details.getAmount());

        paymentRepository.save(payment);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}
