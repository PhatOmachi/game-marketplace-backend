package poly.gamemarketplacebackend.core.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.dto.VNPayResponse;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;
import poly.gamemarketplacebackend.core.repository.TransactionHistoryRepository;
import poly.gamemarketplacebackend.core.repository.UsersRepository;
import poly.gamemarketplacebackend.core.security.VNPAYConfig;
import poly.gamemarketplacebackend.core.service.TransactionHistoryService;
import poly.gamemarketplacebackend.core.service.UsersService;
import poly.gamemarketplacebackend.core.util.DataStore;
import poly.gamemarketplacebackend.core.util.VNPayUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
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
    private final TransactionHistoryRepository repo;
    private final UsersService userService;
    private final UsersRepository usersRepository;
    private final DataStore dataStore;

    @Override
    public VNPayResponse createVnPayPayment(HttpServletRequest request, String name) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang-" + name);
        // Build query URL
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Override
    @Transactional
    @SneakyThrows
    public String pay(HttpServletRequest request, @RequestParam String name) {
        String url = createVnPayPayment(request, name).paymentUrl;
        String query = new URL(url).getQuery();
        Map<String, String> parameters = getQueryParameters(query);

        String vnpAmount = parameters.get("vnp_Amount");
        String vnp_TxnRef = parameters.get("vnp_TxnRef");
        String vnp_OrderInfo = parameters.get("vnp_OrderInfo");
        int hyphenPos = vnp_OrderInfo.indexOf("-");

        String vnp_CreateDate = parameters.get("vnp_CreateDate").substring(0, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(vnp_CreateDate, formatter);
        Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());

        TransactionHistory newTransaction = TransactionHistory.builder()
                .description(vnp_TxnRef)
                .paymentTime(timestamp)
                .amount(Float.parseFloat(vnpAmount) / 100)
                .username(vnp_OrderInfo.substring(hyphenPos + 1))
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

        String vnp_OrderInfo = request.getParameter("vnp_OrderInfo"); //người lập hóa đơn
        int hyphenPos = vnp_OrderInfo.indexOf("-"); //người lập hóa đơn
        String username = vnp_OrderInfo.substring(hyphenPos + 1);
        String tien = request.getParameter("vnp_Amount"); // tiền
        double amount = Double.parseDouble(tien)/100;

        String maDonHang = request.getParameter("vnp_TxnRef");

        if (status.equals("00")) {
            updatePaymentByUser(maDonHang);

            double currentBalance = Double.parseDouble(usersRepository.findByUsername(username).getBalance());
            currentBalance += amount;

            usersRepository.updateUsersByUsername(currentBalance+"",username);
            // Thay đổi giá trị của flag thành true
            dataStore.put("flag", true);
            redirectAttributes.addAttribute("flag", false);
            response.sendRedirect("/add-funds");
            return "true";
        } else {
            dataStore.put("flag1", true);
            response.sendRedirect("/add-funds");
            return "false";
        }
    }

    @Override
    public void save(TransactionHistory transactionHistory) {
        repo.save(transactionHistory);
    }

    @Override
    public void updatePaymentByUser(String name) {
        repo.updatePaymentuser(true, name);
    }

    @Override
    public TransactionHistory findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public List<TransactionHistory> findAllByUsername(String username) {
        return repo.findAllByUsername(username);
    }
}