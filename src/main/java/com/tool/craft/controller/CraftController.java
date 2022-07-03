package com.tool.craft.controller;

import com.tool.craft.entity.BillDetails;
import com.tool.craft.service.craft.CraftService;
import com.tool.craft.service.ocr.TextAndKeyValuePairsService;
import com.tool.craft.service.ocr.TextsAndKeyValuePairs;
import com.tool.craft.service.payment.PaymentService;
import com.tool.craft.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Controller
@RequiredArgsConstructor
public class CraftController {

    private final CraftService craftService;
    private final StorageService storageService;
    private final PaymentService paymentService;

    private final TextAndKeyValuePairsService textDetectionService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("payments",  paymentService.findAll());
        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView start(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Envie um arquivo");
            return new RedirectView("/");
        }

        final TextsAndKeyValuePairs textsAndKeyValuePairs = textDetectionService.findTextsAndKeyValuePairsIn(file.getInputStream());
        final Optional<BillDetails> optionalBillDetails = craftService.findBillDetailsIn(textsAndKeyValuePairs);

        optionalBillDetails
                .ifPresentOrElse(billDetails -> {
                    String receipt = null;
                            receipt = storageService.save(file);
                            paymentService.save(billDetails, receipt);

                    redirectAttributes.addFlashAttribute("message",
                            String.format("Encontrado conta de %s no valor de R$ %s",
                                    billDetails.getType(), billDetails.getAmount()));
                },
                () -> redirectAttributes.addFlashAttribute("message",
                        "Detalhes da conta não encontrado"));

        return new RedirectView("/");
    }

    @DeleteMapping("/{payment_id:\\d+}")
    public RedirectView deletePayment(@PathVariable(name = "payment_id") Long paymentId,
                                      RedirectAttributes redirectAttributes){
        paymentService.findBy(paymentId).ifPresent(payment -> {
            storageService.delete(payment.getReceipt());
            paymentService.delete(paymentId);
            redirectAttributes.addFlashAttribute("message", "Arquivo excluido com sucesso");
        });
        return new RedirectView("/");
    }


}
