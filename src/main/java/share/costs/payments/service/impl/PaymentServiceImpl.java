package share.costs.payments.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.costs.balances.entities.Balance;
import share.costs.balances.entities.BalancesRepository;
import share.costs.balances.entities.GroupUserBalance;
import share.costs.balances.entities.GroupUserBalanceRepository;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupsRepository;
import share.costs.payments.entities.Payment;
import share.costs.payments.entities.PaymentsRepository;
import share.costs.payments.rest.PaymentRequest;
import share.costs.payments.service.PaymentService;
import share.costs.users.entities.User;
import share.costs.users.entities.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.valueOf;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final GroupsRepository groupsRepository;
    private final UserRepository userRepository;
    private final GroupUserBalanceRepository groupUserBalanceRepository;
    private final PaymentsRepository paymentsRepository;
    private final BalancesRepository balancesRepository;


    public PaymentServiceImpl(GroupsRepository groupsRepository,
                              UserRepository userRepository,
                              PaymentsRepository paymentsRepository,
                              GroupUserBalanceRepository groupUserBalanceRepository, BalancesRepository balancesRepository) {
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
        this.paymentsRepository = paymentsRepository;
        this.groupUserBalanceRepository = groupUserBalanceRepository;
        this.balancesRepository = balancesRepository;
    }

    @Override
    @Transactional
    public void makePayment(PaymentRequest paymentRequest, String username) {

        if(!userRepository.existsUserByUsername(username)) {
            throw  new HttpBadRequestException("User entity does not exist for username: " + username);
        }

        if(!groupsRepository.existsById(paymentRequest.getGroupId())) {
            throw  new HttpBadRequestException("Group entity does not exist for id: " + paymentRequest.getGroupId());
        }

        final User user = userRepository.findByUsername(username).get();
        final Group group = groupsRepository.findById(paymentRequest.getGroupId()).get();

        if(!group.getUsers().contains(user)) {
            throw  new HttpBadRequestException("User does not in the group: " + group.getName());
        }

        final Payment payment = new Payment();
        payment.setUser(user);
        payment.setGroup(group);
        payment.setAmount(paymentRequest.getAmount());
        payment.setDescription(paymentRequest.getDescription());
        paymentsRepository.save(payment);

        recalculateUserBalances(group, user, payment);

        group.setBalance(group.getBalance().add(payment.getAmount()));
        groupsRepository.save(group);
    }

    @Transactional
    private void recalculateUserBalances(Group group, User user, Payment payment) {
        final int divisor = group.getUsers().size();

        final BigDecimal userPartition = payment.getAmount().divide(valueOf(divisor), 6, RoundingMode.HALF_EVEN).negate();

        group.getUsers().stream().filter(current -> !user.equals(current)).forEach(current -> {
            GroupUserBalance gub = groupUserBalanceRepository.findByUserAndGroup(current, group).get();
            Balance balance = gub.getBalance();
            balance.setBalance(balance.getBalance().add(userPartition));
            gub.setBalance(balance);
            balancesRepository.save(balance);
        });

        GroupUserBalance gub = groupUserBalanceRepository.findByUserAndGroup(user, group).get();
        Balance balance = gub.getBalance();
        balance.setBalance(balance.getBalance().add(payment.getAmount().add(userPartition)));
        balancesRepository.save(balance);
    }
}
