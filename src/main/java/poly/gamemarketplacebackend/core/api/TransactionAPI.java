package poly.gamemarketplacebackend.core.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.VNPayRequest;
import poly.gamemarketplacebackend.core.service.OrderDetailService;
import poly.gamemarketplacebackend.core.service.TransactionHistoryService;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@Slf4j
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionAPI {
    private final TransactionHistoryService transactionHistoryService;
    private final OrderDetailService orderDetailService;

    @PostMapping("/vn-pay")
    public String pay(@RequestBody VNPayRequest vnPayRequest) {
        return transactionHistoryService.pay(vnPayRequest);
    }

    @GetMapping("/p/vn-pay-callback")
    public String payCallbackHandler(HttpServletRequest request, HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) throws SQLException, IOException {
        return transactionHistoryService.payCallbackHandler(request, response, session, redirectAttributes);
    }

    @GetMapping("/get-transactions")
    public ResponseObject<?> getTransactions(@RequestParam String username) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(transactionHistoryService.findAllByUsername(username))
                .message("Get transactions successfully")
                .build();
    }

    @GetMapping("/get-orders-transaction")
    public ResponseObject<?> getOrdersTransaction(@RequestParam String username) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(transactionHistoryService.findOrdersTransactionByUsername(username))
                .message("Get orders transactions successfully")
                .build();
    }

    @GetMapping("/get-order-detail")
    public ResponseObject<?> getOrderDetail(@RequestParam String orderCode) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderDetailService.findByOrderCode(orderCode))
                .message("Get order detail successfully")
                .build();
    }

}
