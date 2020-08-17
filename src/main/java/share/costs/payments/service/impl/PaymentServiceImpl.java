package share.costs.payments.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.costs.balances.entities.Balance;
import share.costs.balances.entities.BalancesRepository;
import share.costs.groups.entities.GroupUserBalance;
import share.costs.groups.entities.GroupUserBalanceRepository;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupsRepository;
import share.costs.payments.entities.*;
import share.costs.payments.model.UserInPaymentModel;
import share.costs.payments.rest.PaymentRequest;
import share.costs.payments.service.PaymentService;
import share.costs.payments.service.converters.UserInPaymentConverter;
import share.costs.users.entities.User;
import share.costs.users.entities.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final GroupsRepository groupsRepository;
    private final UserRepository userRepository;
    private final GroupUserBalanceRepository groupUserBalanceRepository;
    private final PaymentsRepository paymentsRepository;
    private final BalancesRepository balancesRepository;
    private final UserInPaymentConverter userInPaymentConverter;
    private final UserInPaymentRepository userInPaymentRepository;



    public PaymentServiceImpl(GroupsRepository groupsRepository,
                              UserRepository userRepository,
                              PaymentsRepository paymentsRepository,
                              GroupUserBalanceRepository groupUserBalanceRepository,
                              BalancesRepository balancesRepository,
                              UserInPaymentConverter userInPaymentConverter, UserInPaymentRepository userInPaymentRepository) {
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
        this.paymentsRepository = paymentsRepository;
        this.groupUserBalanceRepository = groupUserBalanceRepository;
        this.balancesRepository = balancesRepository;
        this.userInPaymentConverter = userInPaymentConverter;
        this.userInPaymentRepository = userInPaymentRepository;
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

        Optional<Integer> totalSumInUsers = paymentRequest.getUsers().stream().map(UserInPaymentModel::getAmount).reduce(Integer::sum);

        if(paymentRequest.getTotal().equals(0)) {
            throw  new HttpBadRequestException("Incorrect total amount  in paymentRequest: " + paymentRequest);
        }

        if(Math.abs(paymentRequest.getTotal() - totalSumInUsers.get()) > 1) {
            throw  new HttpBadRequestException("Incorrect amounts in paymentRequest: " + paymentRequest);
        }

        final Payment payment = new Payment();
        payment.setGroup(group);

        //total in PaymentRequest: Integer
        BigDecimal totalAmount = new BigDecimal(Integer.toString(paymentRequest.getTotal())).movePointLeft(2);
        payment.setAmount(totalAmount);
        payment.setDescription(paymentRequest.getDescription());
        payment.setType(paymentRequest.getType());
        payment.setUser(user);
        Payment paymentEntity = paymentsRepository.save(payment);

        payment.setUsersInPayment( paymentRequest.getUsers().stream()
                            .map(userInPaymentConverter::convertToEntity)
                            .peek(userInPayment -> userInPayment.setPayment(paymentEntity))
                            .map(userInPaymentRepository::save)
                            .collect(Collectors.toList()));

        setUserBalances(payment);

        group.setBalance(group.getBalance().add(payment.getAmount()));
        groupsRepository.save(group);
    }

    @Transactional
    private void setUserBalances(Payment payment) {

        payment.getUsersInPayment().forEach(current -> {
            if(!userRepository.existsById(current.getUser().getId())) {
                throw new  HttpBadRequestException("User entity does not exist for if: " + current.getId());
            }

            GroupUserBalance gub = groupUserBalanceRepository.findByUserAndGroup(current.getUser(), payment.getGroup()).get();
            Balance balance = gub.getBalance();

            if(payment.getUser().getId().equals(current.getUser().getId())) {
                balance.setSpending(balance.getSpending().add(payment.getAmount()));
            }

            balance.setCosts((balance.getCosts()).add(current.getAmount().negate()));
            balance.setBalance((balance.getSpending()).add(balance.getCosts()));
            gub.setBalance(balance);
            balancesRepository.save(balance);
        });
    }
}