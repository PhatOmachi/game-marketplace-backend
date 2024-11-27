package poly.gamemarketplacebackend.core.service;

import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersService {
    @Transactional
    void handlePayment(PaymentRequestDTO paymentRequestDTO);

    List<OrdersDTO> findOrderByUsername(String username);

    List<OrdersDTO> findOrdersWithGameNameAndDateRange(String username, String des, LocalDateTime startDate, LocalDateTime endDate);

    List<OrdersDTO> findByOrderCode(String orderCode);

    List<OrdersDTO> findAll();

    OrdersDTO findBySysIdOrder(Integer sysIdOrder);

}