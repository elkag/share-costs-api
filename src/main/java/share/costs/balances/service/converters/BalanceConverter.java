package share.costs.balances.service.converters;

import lombok.Data;
import org.springframework.stereotype.Component;
import share.costs.balances.entities.Balance;
import share.costs.balances.model.BalanceModel;

@Component
@Data
public class BalanceConverter {

    public  BalanceConverter() {

    }

    public final BalanceModel convertToModel(final Balance balance) {
        if(balance == null) {
            return null;
        }

        final BalanceModel model = new BalanceModel();
        model.setId(balance.getId());
        model.setBalance(balance.getBalance());
        model.setSpending(balance.getSpending());
        model.setCosts(balance.getCosts());

        return model;

    }
}
