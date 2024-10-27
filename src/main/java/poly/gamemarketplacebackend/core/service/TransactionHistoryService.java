package poly.gamemarketplacebackend.core.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.gamemarketplacebackend.core.dto.RolesDTO;
import poly.gamemarketplacebackend.core.dto.VNPayResponse;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface TransactionHistoryService {
    VNPayResponse createVnPayPayment(HttpServletRequest request, String name);

    String pay(HttpServletRequest request, @RequestParam String name);

    String payCallbackHandler(HttpServletRequest request, HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) throws IOException, SQLException;

    void save(TransactionHistory transactionHistory);
    void updatePaymentByUser(String name);
    TransactionHistory findByUsername(String username);
    List<TransactionHistory> findAllByUsername(String username);
}