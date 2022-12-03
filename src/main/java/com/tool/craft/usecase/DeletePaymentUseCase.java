package com.tool.craft.usecase;

import com.tool.craft.domain.Payment;
import com.tool.craft.persistence.PaymentRepository;
import com.tool.craft.service.filestorage.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePaymentUseCase {

    private final PaymentRepository repository;
    private final AwsS3Service awsS3Service;

    public void execute(Long paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Não existe pagamento com o id informado"));
        repository.delete(payment);
        awsS3Service.delete(payment.getReceipt());
    }

}
