package poly.gamemarketplacebackend.core.service;

import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;
import poly.gamemarketplacebackend.core.dto.RolesDTO;

import java.util.List;

public interface OrdersService {
    @Transactional
    void handlePayment(PaymentRequestDTO paymentRequestDTO);
}