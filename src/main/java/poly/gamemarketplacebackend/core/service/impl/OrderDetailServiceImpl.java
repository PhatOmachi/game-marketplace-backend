package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.OrderDetailDTO;
import poly.gamemarketplacebackend.core.service.OrderDetailService;
import poly.gamemarketplacebackend.core.service.OrdersService;
import poly.gamemarketplacebackend.core.service.TransactionHistoryService;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrdersService ordersService;
    private final TransactionHistoryService transactionHistoryService;

    @Override
    public OrderDetailDTO findByOrderCode(String orderCode) {
        return OrderDetailDTO.builder()
                .transactionHistoryDTO(transactionHistoryService.findByDescription(orderCode))
                .ordersDTOS(ordersService.findByOrderCode(orderCode))
                .build();
    }

}