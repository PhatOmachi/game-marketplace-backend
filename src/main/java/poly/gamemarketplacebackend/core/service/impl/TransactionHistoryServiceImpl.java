package poly.gamemarketplacebackend.core.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.dto.VNPayRequest;
import poly.gamemarketplacebackend.core.dto.VNPayResponse;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;
import poly.gamemarketplacebackend.core.repository.TransactionHistoryRepository;
import poly.gamemarketplacebackend.core.repository.UsersRepository;
import poly.gamemarketplacebackend.core.security.VNPAYConfig;
import poly.gamemarketplacebackend.core.service.TransactionHistoryService;
import poly.gamemarketplacebackend.core.service.UsersService;
import poly.gamemarketplacebackend.core.util.DataStore;
import poly.gamemarketplacebackend.core.util.VNPayUtil;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
    private final VNPAYConfig vnPayConfig;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UsersRepository usersRepository;
    private final DataStore dataStore;
    private final HttpServletRequest request;

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
        // Initialize a TransactionHistory object with existed data
        TransactionHistory newTransaction = TransactionHistory.builder()
                .description(vnpTxnRef)
                .paymentTime(timestamp)
                .amount(Float.parseFloat(vnpAmount) / 100)
                .username(vnpOrderInfo.substring(hyphenPos + 1))
                .status(false)
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
    public String payCallbackHandler(HttpServletRequest request, HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) {
        String status = request.getParameter("vnp_ResponseCode");        
        if (status.equals("00")) { // "00" : Payment success
            // Get data from input parameters
            String vnpOrderInfo = request.getParameter("vnp_OrderInfo");
            int hyphenPos = vnpOrderInfo.indexOf("-");
            String username = vnpOrderInfo.substring(hyphenPos + 1);
            String vnpAmount = request.getParameter("vnp_Amount");
            double amount = Double.parseDouble(vnpAmount)/100;
            String maDonHang = request.getParameter("vnp_TxnRef");
            // Align data according to the payment succeed
            updatePaymentByUser(maDonHang);
            double currentBalance = Double.parseDouble(usersRepository.findByUsername(username).getBalance());
            currentBalance += amount;
            usersRepository.updateUsersByUsername(currentBalance+"",username);
            response.sendRedirect(dataStore.get("succeedPaymentUrl").toString());
            return "true";
        } else {
            response.sendRedirect(dataStore.get("errorPaymentUrl").toString());
            return "false";
        }
    }

    @Override
    public void save(TransactionHistory transactionHistory) {
        transactionHistoryRepository.save(transactionHistory);
    }

    @Override
    public void updatePaymentByUser(String name) {
        transactionHistoryRepository.updatePaymentuser(true, name);
    }

    @Override
    public TransactionHistory findByUsername(String username) {
        return transactionHistoryRepository.findByUsername(username);
    }

    @Override
    public List<TransactionHistory> findAllByUsername(String username) {
        return transactionHistoryRepository.findAllByUsername(username);
    }
}