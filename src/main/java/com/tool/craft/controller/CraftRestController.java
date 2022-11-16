package com.tool.craft.controller;

import com.tool.craft.entity.BillDetails;
import com.tool.craft.entity.Payment;
import com.tool.craft.service.craft.CraftService;
import com.tool.craft.service.ocr.AnalysedDocument;
import com.tool.craft.service.ocr.AnalyzeDocumentService;
import com.tool.craft.service.payment.PaymentService;
import com.tool.craft.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CraftRestController {

    private final CraftService craftService;
    private final StorageService storageService;
    private final PaymentService paymentService;
    private final AnalyzeDocumentService textDetectionService;

    @GetMapping("/live")
    public Map<String, String> live(){
        HashMap<String, String> map = new HashMap<>();
        map.put("status", "OK");
        return map;
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments(){
        return ResponseEntity.ok(paymentService.findAll());
    }

    @PostMapping("/craft/start")
    public ResponseEntity<BillDetails> start(@RequestParam(value = "file") MultipartFile file) throws IOException {

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
		
    @DeleteMapping("/payments/{payment_id:\\d+}")
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
