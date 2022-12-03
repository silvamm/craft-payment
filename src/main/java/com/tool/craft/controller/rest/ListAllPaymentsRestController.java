package com.tool.craft.controller.rest;

import com.tool.craft.domain.Payment;
import com.tool.craft.usecase.ListAllPaymentsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class ListAllPaymentsRestController {
    private final ListAllPaymentsUseCase listAllPaymentsUseCase;

    @GetMapping("/")
    public ResponseEntity<List<Payment>> getAllPayments(){
        return ResponseEntity.ok(listAllPaymentsUseCase.execute());
    }
}
