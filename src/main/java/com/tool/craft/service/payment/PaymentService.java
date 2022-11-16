package com.tool.craft.service.payment;

import com.tool.craft.entity.BillDetails;
import com.tool.craft.entity.Payment;
import com.tool.craft.repository.payment.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public void save(BillDetails details, String s3Receipt){
        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setBillType(details.getType());
        payment.setReceipt(s3Receipt);
        payment.setAmount(details.getAmount());

        paymentRepository.save(payment);
    }

    public void delete(Long paymentId){
        paymentRepository.deleteById(paymentId);
    }

    public Optional<Payment> findBy(Long paymnetId){
        return paymentRepository.findById(paymnetId);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}
