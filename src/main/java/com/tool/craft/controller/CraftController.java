package com.tool.craft.controller;

import com.tool.craft.entity.BillDetails;
import com.tool.craft.service.craft.text.Text;
import com.tool.craft.service.craft.CraftService;
import com.tool.craft.service.payment.PaymentService;
import com.tool.craft.service.storage.StorageService;
import com.tool.craft.service.ocr.TextDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Controller
@RequiredArgsConstructor
public class CraftController {

    private final CraftService craftService;
    private final StorageService storageService;
    private final TextDetectionService textDetectionService;
    private final PaymentService paymentService;

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

        final InputStream inputStream = file.getInputStream();
        final List<Text> texts = textDetectionService.findTextsIn(inputStream);
        final Optional<BillDetails> optionalBillDetails = craftService.findBillDetailsIn(texts);

        optionalBillDetails
                .ifPresentOrElse(billDetails -> {
                    String receipt = null;
                    try {
                        receipt = storageService.save(file.getBytes(),
                                file.getSize(),
                                getExtension(file.getOriginalFilename()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    paymentService.save(billDetails, receipt);

                    redirectAttributes.addFlashAttribute("message",
                            String.format("Encontrado conta de %s no valor de R$ %s",
                                    billDetails.getType(), billDetails.getAmount()));
                },
                () -> redirectAttributes.addFlashAttribute("message",
                        "Detalhes da conta não encontrado"));

        return new RedirectView("/");
    }

    @DeleteMapping("/{payment_id}")
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
