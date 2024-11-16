package poly.gamemarketplacebackend.core.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.dto.VNPayRequest;
import poly.gamemarketplacebackend.core.dto.VNPayResponse;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface TransactionHistoryService {
    VNPayResponse createVnPayPayment(VNPayRequest vnPayRequest);

    String pay(VNPayRequest vnPayRequest);

    String payCallbackHandler(HttpServletRequest request, HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) throws IOException, SQLException;

    void save(TransactionHistory transactionHistory);

    void updatePaymentByUser(String name);

    TransactionHistory findByUsername(String username);

    List<TransactionHistory> findAllByUsername(String username);
}