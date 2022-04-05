package com.tool.craft.controller;

import com.tool.craft.model.DetectedText;
import com.tool.craft.model.entity.BillDetails;
import com.tool.craft.service.AwsRekognitionService;
import com.tool.craft.service.AwsS3Service;
import com.tool.craft.service.CraftService;
import com.tool.craft.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import software.amazon.awssdk.services.rekognition.model.TextDetection;
import software.amazon.awssdk.services.rekognition.model.TextTypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@Controller
@RequiredArgsConstructor
public class CraftController {

    private final CraftService craftService;
    private final AwsS3Service awsS3Service;
    private final AwsRekognitionService awsRekognitionService;
    private final PaymentService paymentService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("payments",  paymentService.findAll());
        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView start(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Envie um arquivo");
            return new RedirectView("/");
        }

        final InputStream inputStream = file.getInputStream();

        final List<DetectedText> texts = awsRekognitionService
                .detectTextLabelsIn(inputStream)
                .stream()
                .filter(textDetection -> textDetection.type().equals(TextTypes.LINE))
                .map(DetectedText::new)
                .collect(toList());

        final Optional<BillDetails> optionalBillDetails = craftService.findBillDetailsIn(texts);

        optionalBillDetails
                .ifPresentOrElse(billDetails -> {
                    String s3Receipt = null;
                    try {
                        s3Receipt = awsS3Service.saveInBucket(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    paymentService.save(billDetails, s3Receipt);
                    redirectAttributes.addFlashAttribute("message", "Encontrado conta de " + billDetails.getType()
                            + " no valor de R$ " + billDetails.getAmount());
                },
                () -> redirectAttributes.addFlashAttribute("message", "Detalhes da conta não encontrado"));

        return new RedirectView("/");
    }

    @DeleteMapping("/{payment_id}")
    public RedirectView deletePayment(@PathVariable(name = "payment_id") Long paymentId){
        paymentService.findBy(paymentId).ifPresent(p -> {
            awsS3Service.deleteInBucket(p.getReceipt());
            paymentService.delete(paymentId);
        });
        return new RedirectView("/");
    }


}
