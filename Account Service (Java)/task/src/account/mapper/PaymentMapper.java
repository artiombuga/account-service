package account.mapper;

import account.dto.PaymentRequest;
import account.dto.PaymentResponse;
import account.model.Payment;

import java.time.LocalDate;

public class PaymentMapper {
    public Payment mapDtoToPayment(PaymentRequest dto) {
        LocalDate period = parseDate(dto.getPeriod());

        return new Payment();
    }

    public static PaymentResponse mapPaymentToDto(Payment payment) {
        return new PaymentResponse(payment.getEmployee().getName(), payment.getEmployee().getLastname(),
                payment.getPeriod(), payment.getSalary());
    }

    public LocalDate parseDate(String date) {
        String[] dateArray = date.split("-");

        int month = Integer.parseInt(dateArray[0]);
        int year = Integer.parseInt(dateArray[1]);

        return LocalDate.of(year, month, 1);
    }
}
