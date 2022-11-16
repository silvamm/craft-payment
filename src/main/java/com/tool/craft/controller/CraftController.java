package com.tool.craft.controller;

import com.tool.craft.entity.BillDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CraftController {

    private final CraftRestController craftRestController;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("payments", craftRestController.getAllPayments().getBody());
        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView start(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Envie um arquivo");
            return new RedirectView("/");
        }
        ResponseEntity<BillDetails> billDetailsResponseEntity = craftRestController.start(file);
        Optional.ofNullable(billDetailsResponseEntity.getBody())
                .ifPresentOrElse(billDetails ->
                                redirectAttributes.addFlashAttribute("message",
                                        String.format("Encontrado conta de %s no valor de R$ %s",
                                                billDetails.getType(), billDetails.getAmount()))
                        , () -> redirectAttributes.addFlashAttribute("message",
                                "Detalhes da conta não encontrado"));


        return new RedirectView("/");
    }

    @DeleteMapping("/{payment_id:\\d+}")
    public RedirectView deletePayment(@PathVariable(name = "payment_id") Long paymentId,
                                      RedirectAttributes redirectAttributes) {

        ResponseEntity<Void> delete = craftRestController.delete(paymentId);
        if(delete.getStatusCode().is2xxSuccessful())
            redirectAttributes.addFlashAttribute("message", "Arquivo excluido com sucesso");

        return new RedirectView("/");
    }


}
