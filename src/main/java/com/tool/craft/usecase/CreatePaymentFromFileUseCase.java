package com.tool.craft.usecase;

import com.tool.craft.domain.BillDetails;
import com.tool.craft.domain.Payment;
import com.tool.craft.persistence.PaymentRepository;
import com.tool.craft.service.craft.CraftService;
import com.tool.craft.service.filestorage.AwsS3Service;
import com.tool.craft.service.ocr.AwsTextractService;
import com.tool.craft.service.ocr.TextsAndKeyValuePairs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePaymentFromFileUseCase {

    private final PaymentRepository repository;
    private final AwsTextractService awsTextractService;
    private final CraftService craftService;
    private final AwsS3Service awsS3Service;

    public Optional<Payment> execute(MultipartFile file) throws IOException {

        final TextsAndKeyValuePairs textsAndKeyValuePairs = awsTextractService.analyseDocumentoFrom(file.getInputStream());
        final Optional<BillDetails> optionalBillDetails = craftService.findBillDetailsIn(textsAndKeyValuePairs);

        return optionalBillDetails.map(billDetails -> {
            String receipt = awsS3Service.save(file);
            Payment payment = Payment.newInstance(billDetails, receipt);
            return repository.save(payment);
        });

    }
}
