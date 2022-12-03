package com.tool.craft.controller.rest;

import com.tool.craft.domain.Payment;
import com.tool.craft.usecase.CreatePaymentFromFileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class CreatePaymentFromFileRestController {

    private final CreatePaymentFromFileUseCase createPaymentFromFileUseCase;


    @PostMapping("/craft/start")
    public ResponseEntity<Payment> start(@RequestParam(value = "file") MultipartFile file) throws IOException {

        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        Optional<Payment> optionalPayment = createPaymentFromFileUseCase.execute(file);

        return ResponseEntity.of(optionalPayment);
    }

}
