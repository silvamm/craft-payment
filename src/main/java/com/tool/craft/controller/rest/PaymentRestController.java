package com.tool.craft.controller.rest;

import com.tool.craft.model.BillDetails;
import com.tool.craft.model.Payment;
import com.tool.craft.service.craft.CraftService;
import com.tool.craft.service.ocr.AwsTextractService;
import com.tool.craft.service.ocr.TextsAndKeyValuePairs;
import com.tool.craft.service.payment.PaymentService;
import com.tool.craft.service.filestorage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentRestController {

    private final CraftService craftService;
    private final StorageService storageService;
    private final PaymentService paymentService;
    private final AwsTextractService textDetectionService;

    @GetMapping("/")
    public ResponseEntity<List<Payment>> getAllPayments(){
        return ResponseEntity.ok(paymentService.findAll());
    }

    @PostMapping("/craft/start")
    public ResponseEntity<BillDetails> start(@RequestParam(value = "file") MultipartFile file) throws IOException {

        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        final TextsAndKeyValuePairs textsAndKeyValuePairs = textDetectionService.analyseDocumentoFrom(file.getInputStream());
        final Optional<BillDetails> optionalBillDetails = craftService.findBillDetailsIn(textsAndKeyValuePairs);

        optionalBillDetails
                .ifPresent(billDetails -> {
                    String receipt = storageService.save(file);
                    paymentService.save(billDetails, receipt);
                });

        return ResponseEntity.of(optionalBillDetails);
    }
		
    @DeleteMapping("/{payment_id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable(name = "payment_id") Long paymentId) {

        Optional<Payment> optionalPayment = paymentService.findBy(paymentId);
        if (optionalPayment.isEmpty())
            return ResponseEntity.notFound().build();

        Payment payment = optionalPayment.get();

        storageService.delete(payment.getReceipt());
        paymentService.delete(paymentId);

        return ResponseEntity.noContent().build();
    }

}