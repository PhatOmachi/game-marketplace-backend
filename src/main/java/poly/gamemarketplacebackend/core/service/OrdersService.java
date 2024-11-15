package poly.gamemarketplacebackend.core.service;

import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;

public interface OrdersService {
    @Transactional
    void handlePayment(PaymentRequestDTO paymentRequestDTO);
}