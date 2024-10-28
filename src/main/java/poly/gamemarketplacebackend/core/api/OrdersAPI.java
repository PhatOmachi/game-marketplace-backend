package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;
import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.service.OrdersService;
import poly.gamemarketplacebackend.core.service.VoucherService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersAPI {

    private final OrdersService ordersService;

    @PostMapping("/handle-payment")
    public ResponseObject<?> handlePayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        ordersService.handlePayment(paymentRequestDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Payment success")
                .build();
    }

}
