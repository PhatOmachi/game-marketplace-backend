package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.OrderDetailDTO;

public interface OrderDetailService {
    OrderDetailDTO findByOrderCode(String orderCode);
}