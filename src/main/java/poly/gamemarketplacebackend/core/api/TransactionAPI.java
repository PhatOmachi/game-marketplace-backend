package poly.gamemarketplacebackend.core.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.*;
import poly.gamemarketplacebackend.core.service.OrderDetailService;
import poly.gamemarketplacebackend.core.service.TransactionHistoryService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

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


    @GetMapping("/find-transaction")
    public ResponseObject<?> findTransactionsByUserAndDesAndDate(
            @RequestParam("username") String username,
            @RequestParam(value = "des", required = false) String des,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<TransactionHistoryDTO> transactions = transactionHistoryService.findTransactionByUserAndDesAndDate(username, des, startDate, endDate);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Transaction History List")
                .data(transactions)
                .build();
    }

    @GetMapping("/statistics/new-orders")
    public ResponseObject<?> getTransactionStatistics() {
        List<Statistic> statistics = transactionHistoryService.getTransactionStatistics();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Transaction Statistics")
                .data(statistics)
                .build();
    }

    @GetMapping("/statistics/sums")
    public ResponseObject<?> getTransactionSums() {
        TransactionSums transactionSums = transactionHistoryService.getTransactionSums();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Transaction sums retrieved successfully")
                .data(transactionSums)
                .build();
    }

    @GetMapping("/statistics/summary")
    public ResponseObject<?> getTransactionSummary() {
        List<TransactionSummary> transactionSummary = transactionHistoryService.getTransactionSummary();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Transaction summary retrieved successfully")
                .data(transactionSummary)
                .build();
    }
}
