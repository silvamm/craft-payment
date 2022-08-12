package com.tool.craft.controller;

import com.tool.craft.entity.BillDetails;
import com.tool.craft.entity.Payment;
import com.tool.craft.service.craft.CraftService;
import com.tool.craft.service.ocr.AnalysedDocument;
import com.tool.craft.service.ocr.AnalyzeDocumentService;
import com.tool.craft.service.payment.PaymentService;
import com.tool.craft.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CraftRestController {

    private final CraftService craftService;
    private final StorageService storageService;
    private final PaymentService paymentService;
    private final AnalyzeDocumentService textDetectionService;

    public ResponseEntity<List<Payment>> getAllPayments(){
        return ResponseEntity.ok(paymentService.findAll());
    }

    public ResponseEntity<BillDetails> start(MultipartFile file) throws IOException {

        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        final AnalysedDocument textsAndKeyValuePairs = textDetectionService.analyseDocumentoFrom(file.getInputStream());
        final Optional<BillDetails> optionalBillDetails = craftService.findBillDetailsIn(textsAndKeyValuePairs);

        optionalBillDetails
                .ifPresent(billDetails -> {
                    String receipt = storageService.save(file);
                    paymentService.save(billDetails, receipt);
                });

        return ResponseEntity.of(optionalBillDetails);
    }

    public ResponseEntity<Void> delete(Long paymentId) {

        Optional<Payment> optionalPayment = paymentService.findBy(paymentId);
        if (optionalPayment.isEmpty())
            return ResponseEntity.notFound().build();

        Payment payment = optionalPayment.get();

        storageService.delete(payment.getReceipt());
        paymentService.delete(paymentId);

        return ResponseEntity.noContent().build();
    }

}