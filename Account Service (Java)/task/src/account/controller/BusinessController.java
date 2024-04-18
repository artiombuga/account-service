package account.controller;

import account.dto.PaymentRequest;
import account.model.Payment;
import account.service.BusinessService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class BusinessController {
    private final BusinessService service;

    public BusinessController(BusinessService service) {
        this.service = service;
    }

    @GetMapping(value = "/empl/payment")
    public ResponseEntity<?> getUserPayroll(@AuthenticationPrincipal UserDetails details,
                                            @RequestParam(required = false) String period) {
        return service.getPayments(details, period);
    }

    @PostMapping(value = "/acct/payments")
    public ResponseEntity<?> uploadPayments(@RequestBody List<@Valid PaymentRequest> paymentRequests) {
        return service.uploadPayments(paymentRequests);
    }

    @PutMapping(value = "/acct/payments")
    public ResponseEntity<?> updatePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        return service.updatePayment(paymentRequest);
    }
}
