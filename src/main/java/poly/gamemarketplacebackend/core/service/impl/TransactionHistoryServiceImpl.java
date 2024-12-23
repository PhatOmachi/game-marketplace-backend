package poly.gamemarketplacebackend.core.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.dto.*;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.TransactionHistoryMapper;
import poly.gamemarketplacebackend.core.repository.TransactionHistoryRepository;
import poly.gamemarketplacebackend.core.repository.UsersRepository;
import poly.gamemarketplacebackend.core.security.VNPAYConfig;
import poly.gamemarketplacebackend.core.service.TransactionHistoryService;
import poly.gamemarketplacebackend.core.util.DataStore;
import poly.gamemarketplacebackend.core.util.VNPayUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
    private final VNPAYConfig vnPayConfig;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UsersRepository usersRepository;
    private final DataStore dataStore;
    private final HttpServletRequest request;
    private final TransactionHistoryMapper transactionHistoryMapper;

    @Override
    public VNPayResponse createVnPayPayment(VNPayRequest vnPayRequest) {
        Map<String, String> vnpParamsMap = getVNPayMap(vnPayRequest);
        // Build query URL
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        dataStore.put("succeedPaymentUrl", vnPayRequest.getSuccessUrl());
        dataStore.put("errorPaymentUrl", vnPayRequest.getErrorUrl());
        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    private Map<String, String> getVNPayMap(VNPayRequest vnPayRequest) {
        long amount = vnPayRequest.getAmount() * 100L;
        String bankCode = vnPayRequest.getBankCode();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_OrderInfo", "Check out order-" + vnPayRequest.getName());
        return vnpParamsMap;
    }

    @Override
    @Transactional
    @SneakyThrows
    public String pay(VNPayRequest vnPayRequest) {
        String url = createVnPayPayment(vnPayRequest).paymentUrl;
        String query = new URL(url).getQuery();
        Map<String, String> parameters = getQueryParameters(query);
        // Get & initialize data from VNPayRequest
        String vnpAmount = parameters.get("vnp_Amount");
        String vnpTxnRef = parameters.get("vnp_TxnRef");
        String vnpOrderInfo = parameters.get("vnp_OrderInfo");
        int hyphenPos = vnpOrderInfo.indexOf("-");
        String vnpCreateDate = parameters.get("vnp_CreateDate").substring(0, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(vnpCreateDate, formatter);
        Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
        String username = vnpOrderInfo.substring(hyphenPos + 1);
        // Initialize a TransactionHistory object with existed data
        TransactionHistory newTransaction = TransactionHistory.builder()
                .description(vnpTxnRef)
                .paymentTime(timestamp)
                .amount(Float.parseFloat(vnpAmount) / 100)
                .username(username)
                .status(false)
                .userBalance(Double.parseDouble(usersRepository.findByUsername(username).getBalance()))
                .build();
        save(newTransaction);
        return url;
    }

    @SneakyThrows
    private Map<String, String> getQueryParameters(String query) {
        Map<String, String> parameters = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return parameters;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            parameters.put(key, value);
        }
        return parameters;
    }

    @Override
    @SneakyThrows
    @Transactional
    public String payCallbackHandler(HttpServletRequest request, HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) { // "00" : Payment success
            // Get data from input parameters
            String vnpOrderInfo = request.getParameter("vnp_OrderInfo");
            int hyphenPos = vnpOrderInfo.indexOf("-");
            String username = vnpOrderInfo.substring(hyphenPos + 1);
            String vnpAmount = request.getParameter("vnp_Amount");
            double amount = Double.parseDouble(vnpAmount) / 100;
            String maDonHang = request.getParameter("vnp_TxnRef");
            // Align data according to the payment succeed
            double currentBalance = Double.parseDouble(usersRepository.findByUsername(username).getBalance());
            currentBalance += amount;
            updatePaymentByUser(maDonHang, currentBalance);
            usersRepository.updateUsersByUsername(currentBalance + "", username);
            response.sendRedirect(dataStore.get("succeedPaymentUrl").toString());
            return "true";
        } else {
            response.sendRedirect(dataStore.get("errorPaymentUrl").toString());
            return "false";
        }
    }

    @Override
    @Transactional
    public void save(TransactionHistory transactionHistory) {
        transactionHistoryRepository.save(transactionHistory);
    }

    @Override
    @Transactional
    public void updatePaymentByUser(String name, double userBalance) {
        transactionHistoryRepository.updatePaymentuser(true, userBalance, name);
    }

    @Override
    public TransactionHistory findByUsername(String username) {
        return transactionHistoryRepository.findByUsername(username);
    }

    @Override
    public List<TransactionHistory> findAllByUsername(String username) {
        return transactionHistoryRepository.findAllByUsername(username);
    }

    public List<TransactionHistoryDTO> findOrdersTransactionByUsername(String username) {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findByDescriptionStartingWithAndUsername(username, username);
        return transactionHistories.stream()
                .map(transactionHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionHistoryDTO findByDescription(String description) {
        var th = transactionHistoryRepository.findByDescription(description).orElseThrow(
                () -> new CustomException("Transaction not found", HttpStatus.NOT_FOUND)
        );
        return transactionHistoryMapper.toDTO(th);
    }

    @Override
    public List<TransactionHistoryDTO> findTransactionByUserAndDesAndDate(
            String username, String des, LocalDateTime startDate, LocalDateTime endDate) {

        // Lấy danh sách lịch sử giao dịch dựa trên username
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByUsername(username);

        // Xây dựng predicate cho các điều kiện lọc
        Predicate<TransactionHistory> filterPredicate = transaction -> true; // Mặc định tất cả hợp lệ

        // Lọc theo mô tả (description)
        if (des != null) {
            filterPredicate = filterPredicate.and(transaction -> transaction.getDescription().contains(des));
        }

        // Lọc theo ngày bắt đầu
        if (startDate != null) {
            filterPredicate = filterPredicate.and(transaction -> {
                LocalDateTime paymentTime = transaction.getPaymentTime().toLocalDateTime();
                return !paymentTime.isBefore(startDate);
            });
        }

        // Lọc theo ngày kết thúc
        if (endDate != null) {
            filterPredicate = filterPredicate.and(transaction -> {
                LocalDateTime paymentTime = transaction.getPaymentTime().toLocalDateTime();
                return !paymentTime.isAfter(endDate);
            });
        }

        // Áp dụng các điều kiện lọc và chuyển đổi thành DTO
        return transactionHistories.stream()
                .filter(filterPredicate) // Áp dụng bộ lọc
                .map(transaction -> transactionHistoryMapper.toDTO(transaction)) // Chuyển đổi thành DTO
                .collect(Collectors.toList());
    }
    @Override
    public List<Statistic> getTransactionStatistics() {
        List<Object[]> results = transactionHistoryRepository.getTransactionStatistics();
        List<Statistic> statistics = new ArrayList<>();
        for (Object[] result : results) {
            statistics.add(Statistic.builder()
                    .thisMonth((Integer) result[0])
                    .increasedPercent((BigDecimal) result[1])
                    .build());
        }
        return statistics;
    }

    @Override
    public TransactionSums getTransactionSums() {
        List<Object[]> results = transactionHistoryRepository.getTransactionSums();
        Object[] result = results.get(0);
        return TransactionSums.builder()
                .totalIncome((BigDecimal) result[0])
                .todayIncome((BigDecimal) result[1])
                .build();
    }

    @Override
    public List<TransactionSummary> getTransactionSummary() {
        List<Object[]> results = transactionHistoryRepository.getTransactionSummary();
        return results.stream()
                .map(result -> TransactionSummary.builder()
                        .date((String) result[0])
                        .orderCount((Integer) result[1])
                        .userCount((Integer) result[2])
                        .build())
                .collect(Collectors.toList());
    }
}