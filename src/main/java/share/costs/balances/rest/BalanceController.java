package share.costs.balances.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balances")
@PreAuthorize("hasRole('ADMIN')")
public class BalanceController {
}
