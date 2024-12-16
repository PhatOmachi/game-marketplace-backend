package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;
import poly.gamemarketplacebackend.core.service.OrderDetailService;
import poly.gamemarketplacebackend.core.service.OrdersService;

import java.time.LocalDateTime;
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

    @GetMapping("/find-order-by-username")
    public ResponseObject<?> findOrdersByUsername(@RequestParam("username") String username) {
        List<OrdersDTO> ordersList = ordersService.findOrderByUsername(username);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("List Order By Username")
                .data(ordersList)
                .build();
    }

    @GetMapping("/find-all")
    public ResponseObject<?> findAll() {
        List<OrdersDTO> ordersList = ordersService.findAll();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("List Order")
                .data(ordersList)
                .build();
    }

    @GetMapping("/find-by-sys-id-order")
    public ResponseObject<?> findBySysIdOrder(@RequestParam("sysIdOrder") Integer sysIdOrder) {
        OrdersDTO ordersDTO = ordersService.findBySysIdOrder(sysIdOrder);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Order")
                .data(ordersDTO)
                .build();
    }

    @GetMapping("/analytics-summary")
    public ResponseObject<?> getAnalyticsSummary() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Analytics Summary")
                .data(ordersService.getAnalyticsSummary())
                .build();
    }


    @GetMapping("/find-order")
    public ResponseObject<?> findOrdersWithGameNameAndDateRange(
            @RequestParam("username") String username,
            @RequestParam(value = "des", required = false) String des,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate
    ) {
        List<OrdersDTO> ordersList = ordersService.findOrdersWithGameNameAndDateRange(username, des, startDate, endDate);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("List Order")
                .data(ordersList)
                .build();
    }

}
