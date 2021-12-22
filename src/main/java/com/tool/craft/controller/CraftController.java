package com.tool.craft.controller;

import com.tool.craft.enumm.BillType;
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

import java.io.IOException;
import java.io.InputStream;
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
    public String index() {
        return "index";
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
        Optional<BillType> optionalBillType = craftService.startSearchIn(inputStream);
        optionalBillType.ifPresent(billType ->  paymentService.save(billType, inputStream));

        modelAndView.addObject("message", optionalBillType.map(type -> "Encontrado conta de " + type).orElseGet(() -> "Não encontrado"));
        return modelAndView;
    }

}
