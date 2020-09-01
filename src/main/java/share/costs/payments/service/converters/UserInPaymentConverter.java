package share.costs.payments.service.converters;

import org.springframework.stereotype.Component;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.payments.entities.UserInPayment;
import share.costs.payments.model.UserInPaymentModel;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;

import java.math.BigDecimal;

@Component
public class UserInPaymentConverter {

    private final UserRepository userRepository;

    public UserInPaymentConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInPayment convertToEntity(UserInPaymentModel userInPaymentModel){
        if(!userRepository.existsById(userInPaymentModel.getUserId())) {
            throw  new HttpBadRequestException("User entity does not exist for ID " + userInPaymentModel.getId());
        }

        UserEntity user = userRepository.findById(userInPaymentModel.getUserId()).get();

        UserInPayment entity = new UserInPayment();
        entity.setId(userInPaymentModel.getId());
        entity.setAmount(new BigDecimal(Integer.toString(userInPaymentModel.getAmount())).movePointLeft(2));
        entity.setUser(user);
        entity.setWeight(userInPaymentModel.getWeight());
        return entity;
    }
}
