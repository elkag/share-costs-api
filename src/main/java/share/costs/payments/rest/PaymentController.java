package share.costs.payments.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import share.costs.payments.service.PaymentService;

import java.security.Principal;

@RestController
@RequestMapping("/payment")
@PreAuthorize("hasRole('USER')")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/make-payment")
    public void makePayment(@RequestBody final PaymentRequest paymentRequest, Principal principal) {
        paymentService.makePayment(paymentRequest, principal.getName());
    }
}
