package com.tool.craft.usecase;

import com.tool.craft.domain.Payment;
import com.tool.craft.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAllPaymentsUseCase {

    private final PaymentRepository repository;

    public List<Payment> execute(){
        return repository.findAll();
    }
}
