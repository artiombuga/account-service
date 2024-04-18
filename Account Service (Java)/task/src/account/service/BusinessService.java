package account.service;

import account.dto.PaymentRequest;
import account.exception.InvalidPeriodException;
import account.exception.NoSuchPaymentPeriodFoundException;
import account.exception.UserAndPaymentPairAlreadyExistsException;
import account.exception.UserNotFoundException;
import account.mapper.PaymentMapper;
import account.model.Payment;
import account.model.User;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BusinessService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public BusinessService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> uploadPayments(List<PaymentRequest> paymentRequests) {
        List<Payment> payments = new ArrayList<>();

        for (PaymentRequest paymentRequest : paymentRequests) {

            if (!paymentRequest.getPeriod().matches("(0?[1-9]|1[0-2])-\\d+")) {
                throw new InvalidPeriodException();
            }

            User employee = userRepository.findUserByEmailIgnoreCase(paymentRequest.getEmployee())
                    .orElseThrow(UserNotFoundException::new);

            LocalDate period = parseDate(paymentRequest.getPeriod());

            if (paymentRepository.existsByEmployeeAndPeriod(employee, period)) {
                throw new UserAndPaymentPairAlreadyExistsException();
            }

            Payment payment = new Payment();
            payment.setEmployee(employee);
            payment.setPeriod(period);
            payment.setSalary(paymentRequest.getSalary());

            payments.add(payment);
        }

        paymentRepository.saveAll(payments);

        return ResponseEntity.ok(Map.of("status", "Added successfully!"));
    }

    public ResponseEntity<?> getPayments(UserDetails details, String period) {
        User user = userRepository.findUserByEmailIgnoreCase(details.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(details.getUsername()));

        if (Objects.isNull(period)) {
            List<Payment> payments = paymentRepository.findByEmployeeOrderByPeriodDesc(user);
            return ResponseEntity.ok(payments.stream().map(PaymentMapper::mapPaymentToDto));
        }

        if (!period.matches("(0?[1-9]|1[0-2])-\\d+")) {
            throw new InvalidPeriodException();
        }

        Payment payment = paymentRepository.findByEmployeeAndPeriod(user, parseDate(period))
                .orElseThrow(NoSuchPaymentPeriodFoundException::new);

        return ResponseEntity.ok(PaymentMapper.mapPaymentToDto(payment));

    }

    public LocalDate parseDate(String date) {
        String[] dateArray = date.split("-");

        int month = Integer.parseInt(dateArray[0]);
        int year = Integer.parseInt(dateArray[1]);

        return LocalDate.of(year, month, 1);
    }

    public ResponseEntity<?> updatePayment(PaymentRequest paymentRequest) {
        User user = userRepository.findUserByEmailIgnoreCase(paymentRequest.getEmployee())
                .orElseThrow(() -> new UsernameNotFoundException(paymentRequest.getEmployee()));

        if (!paymentRequest.getPeriod().matches("(0?[1-9]|1[0-2])-\\d+")) {
            throw new InvalidPeriodException();
        }

        Payment payment = paymentRepository.findByEmployeeAndPeriod(user, parseDate(paymentRequest.getPeriod()))
                .orElseThrow(NoSuchPaymentPeriodFoundException::new);

        payment.setSalary(paymentRequest.getSalary());
        paymentRepository.save(payment);

        return ResponseEntity.ok(Map.of("status", "Updated successfully!"));
    }
}
