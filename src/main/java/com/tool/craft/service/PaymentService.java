package com.tool.craft.service;

import com.tool.craft.enumm.BillType;
import com.tool.craft.model.Payment;
import com.tool.craft.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AwsService awsService;

    public void save(BillType billType, InputStream inputStream){
        String receipt = awsService.saveInBucketS3(inputStream);

        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setBillType(billType);
        payment.setReceipt(receipt);

        paymentRepository.save(payment);
    }
}
