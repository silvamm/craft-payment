package com.tool.craft.controller.view;

import com.tool.craft.controller.rest.DeletePaymentRestController;
import com.tool.craft.controller.rest.ListAllPaymentsRestController;
import com.tool.craft.controller.rest.CreatePaymentFromFileRestController;
import com.tool.craft.domain.BillDetails;
import com.tool.craft.domain.Payment;
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
public class ViewController {

    private final CreatePaymentFromFileRestController paymentRestController;
    private final ListAllPaymentsRestController listAllPaymentsController;
    private final DeletePaymentRestController deletePaymentRestController;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("payments", listAllPaymentsController.getAllPayments().getBody());
        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView start(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Envie um arquivo");
            return new RedirectView("/");
        }
        ResponseEntity<Payment> paymentResponseEntity = paymentRestController.start(file);
        Optional.ofNullable(paymentResponseEntity.getBody())
                .ifPresentOrElse(payment ->
                                redirectAttributes.addFlashAttribute("message",
                                        String.format("Encontrado conta de %s no valor de R$ %s",
                                                payment.getBillType(), payment.getAmount()))
                        , () -> redirectAttributes.addFlashAttribute("message",
                                "Detalhes da conta não encontrado"));


        return new RedirectView("/");
    }

    @DeleteMapping("/{payment_id:\\d+}")
    public RedirectView deletePayment(@PathVariable(name = "payment_id") Long paymentId,
                                      RedirectAttributes redirectAttributes) {

        ResponseEntity<Void> delete = deletePaymentRestController.execute(paymentId);
        if(delete.getStatusCode().is2xxSuccessful())
            redirectAttributes.addFlashAttribute("message", "Arquivo excluido com sucesso");

        return new RedirectView("/");
    }


}
