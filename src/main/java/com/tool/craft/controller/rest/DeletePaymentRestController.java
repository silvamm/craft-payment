package com.tool.craft.controller.rest;

import com.tool.craft.usecase.DeletePaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class DeletePaymentRestController {

    private final DeletePaymentUseCase deletePaymentUseCase;

    @DeleteMapping("/{payment_id:\\d+}")
    public ResponseEntity<Void> execute(@PathVariable(name = "payment_id") Long paymentId) {
        try {
            deletePaymentUseCase.execute(paymentId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
