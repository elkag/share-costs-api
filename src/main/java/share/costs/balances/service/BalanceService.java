package share.costs.balances.service;

import share.costs.balances.entities.Balance;

import java.math.BigDecimal;

public interface BalanceService {
    void updateBalance(Balance balance, BigDecimal amount);
}
