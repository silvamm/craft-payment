package com.tool.craft.controller;

import com.tool.craft.model.DetectedText;
import com.tool.craft.model.entity.BillDetails;
import com.tool.craft.service.AwsRekognitionService;
import com.tool.craft.service.AwsS3Service;
import com.tool.craft.service.CraftService;
import com.tool.craft.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import software.amazon.awssdk.services.rekognition.model.TextDetection;
import software.amazon.awssdk.services.rekognition.model.TextTypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@Controller
public class CraftController {

    @Autowired
    private CraftService craftService;

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private AwsRekognitionService awsRekognitionService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("payments",  paymentService.findAll());
        return modelAndView;
    }

    @PostMapping("/start")
    public ModelAndView start(@RequestParam("file") MultipartFile file) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        if (file.isEmpty()) {
            modelAndView.addObject("message", "Envie um arquivo");
            return modelAndView;
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
                    modelAndView.addObject("message", "Encontrado conta de " + billDetails.getType()
                            + " no valor de R$ " + billDetails.getAmount());
                },
                () -> modelAndView.addObject("message", "Detalhes da conta não encontrado"));


        modelAndView.addObject("payments",  paymentService.findAll());
        return modelAndView;
    }

    @DeleteMapping("/{payment_id}")
    public ModelAndView deletePayment(@PathVariable(name = "payment_id") Long paymentId){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        paymentService.findBy(paymentId).ifPresent(p -> {
            awsS3Service.deleteInBucket(p.getReceipt());
            paymentService.delete(paymentId);
        });
        modelAndView.addObject("payments",  paymentService.findAll());
        return modelAndView;
    }


}
