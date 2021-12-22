package com.tool.craft.controller;

import com.tool.craft.enumm.BillType;
import com.tool.craft.model.BillDetails;
import com.tool.craft.service.AwsService;
import com.tool.craft.service.CraftService;
import com.tool.craft.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Controller
public class CraftController {

    @Autowired
    private CraftService craftService;

    @Autowired
    private AwsService awsService;

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

        InputStream inputStream = file.getInputStream();

        List<TextDetection> textDetections = awsService.detectTextLabelsIn(inputStream);
        BillDetails billDetails = craftService.findBillDetails(textDetections);

        if(billDetails.filled()){
            String s3Receipt = awsService.saveInBucketS3(inputStream);
            paymentService.save(billDetails, s3Receipt, inputStream);
            modelAndView.addObject("message", "Encontrado conta de " + billDetails.getType()
                    + " no valor de R$ " + billDetails.getAmount());
            return modelAndView;
        }

        modelAndView.addObject("message",  "Detalhes da conta não encontrado");
        return modelAndView;
    }

}
