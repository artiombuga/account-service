package account.repository;

import account.model.Payment;
import account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    boolean existsByEmployeeAndPeriod(User employee, LocalDate period);
    List<Payment> findByEmployeeOrderByPeriodDesc(User employee);
    Optional<Payment> findByEmployeeAndPeriod(User employee, LocalDate period);
}
