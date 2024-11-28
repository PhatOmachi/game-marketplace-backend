package poly.gamemarketplacebackend.core.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.dto.OwnedGameDTO;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;
import poly.gamemarketplacebackend.core.dto.TransactionHistoryDTO;
import poly.gamemarketplacebackend.core.entity.Orders;
import poly.gamemarketplacebackend.core.entity.Voucher_use;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.OrdersMapper;
import poly.gamemarketplacebackend.core.mapper.OwnedGameMapper;
import poly.gamemarketplacebackend.core.mapper.TransactionHistoryMapper;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.repository.*;
import poly.gamemarketplacebackend.core.service.*;
import poly.gamemarketplacebackend.core.util.LicenseKeyUtils;
import poly.gamemarketplacebackend.core.util.TimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {
    private static final Logger log = LoggerFactory.getLogger(OrdersServiceImpl.class);
    private final TransactionHistoryService transactionHistoryService;
    private final TransactionHistoryMapper transactionHistoryMapper;
    private final UsersService usersService;
    private final OrdersRepository ordersRepository;
    private final VoucherService voucherService;
    private final UsersRepository usersRepository;
    private final VoucherMapper voucherMapper;
    private final OrdersMapper ordersMapper;
    private final VoucherUsedRepository voucherUsedRepository;
    private final VoucherRepository voucherRepository;
    private final GameService gameService;
    private final OwnedGameRepository ownedGameRepository;
    private final OwnedGameMapper ownedGameMapper;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public void handlePayment(PaymentRequestDTO paymentRequestDTO) {
        handleUserBalance(paymentRequestDTO);
        handleUsedVoucher(paymentRequestDTO);
        handleOrders(paymentRequestDTO);
    }

    @Override
    public List<OrdersDTO> findOrderByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }

        List<Orders> orders = ordersRepository.findByUsersUsername(username);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return ordersMapper.toDTOs(orders);
    }

    @Override
    public List<OrdersDTO> findAll() {
        return ordersMapper.toDTOs(ordersRepository.findAll());
    }

    @Override
    public OrdersDTO findBySysIdOrder(Integer sysIdOrder) {
        if (sysIdOrder == null) {
            throw new IllegalArgumentException("sysIdOrder must not be null");
        }

        Orders orders = ordersRepository.findBySysIdOrder(sysIdOrder);
        if (orders == null) {
            return null;
        }

        return ordersMapper.toDTO(orders);
    }

    @Override
    public List<OrdersDTO> findOrdersWithGameNameAndDateRange(String username, String des, LocalDateTime startDate, LocalDateTime endDate) {
        List<Orders> orders = ordersRepository.findByUsersUsername(username);

        // Xây dựng predicate cho các điều kiện lọc
        Predicate<Orders> filterPredicate = order -> true; // Mặc định tất cả đơn hàng hợp lệ

        // Nếu có điều kiện lọc theo tên game
        if (des != null) {
            filterPredicate = filterPredicate.and(order -> order.getGame().getGameName().contains(des));
        }

        // Nếu có điều kiện lọc theo ngày bắt đầu
        if (startDate != null) {
            filterPredicate = filterPredicate.and(order -> !order.getOrderDate().toLocalDateTime().isBefore(startDate));
        }

        // Nếu có điều kiện lọc theo ngày kết thúc
        if (endDate != null) {
            filterPredicate = filterPredicate.and(order -> !order.getOrderDate().toLocalDateTime().isAfter(endDate));
        }

        // Lọc và chuyển đổi các đơn hàng
        return orders.stream()
                .filter(filterPredicate)  // Áp dụng tất cả các điều kiện lọc
                .map(order -> ordersMapper.toDTO(order))  // Chuyển đổi thành OrdersDTO
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdersDTO> findByOrderCode(String orderCode) {
        return ordersMapper.toDTOs(ordersRepository.findByOrderCode(orderCode));
    }

    @Transactional
    protected void handleUserBalance(PaymentRequestDTO paymentRequestDTO) {
        var currentUser = usersService.getCurrentUser();
        if (currentUser.getSysIdUser() != paymentRequestDTO.getUserId()) {
            throw new CustomException("Invalid user commit payment", HttpStatus.UNAUTHORIZED);
        }
        var userBalance = Float.parseFloat(currentUser.getBalance());
        userBalance += paymentRequestDTO.getTotalPayment();
        if (userBalance < 0) {
            throw new CustomException("Not enough balance", HttpStatus.BAD_REQUEST);
        }
        usersRepository.updateUsersByUsername(String.valueOf(userBalance), currentUser.getUsername());
        handleTransactionHistory(paymentRequestDTO, userBalance);
    }

    @Transactional
    protected void handleTransactionHistory(PaymentRequestDTO paymentRequestDTO, double userBalance) {
        var transactionHistory = new TransactionHistoryDTO();
        transactionHistory.setAmount(paymentRequestDTO.getTotalPayment());
        transactionHistory.setDescription(paymentRequestDTO.getOrderCode());
        transactionHistory.setPaymentTime(paymentRequestDTO.getOrderDate());
        transactionHistory.setStatus(true);
        transactionHistory.setUsername(paymentRequestDTO.getUsername());
        transactionHistory.setUserBalance(userBalance);
        transactionHistoryService.save(transactionHistoryMapper.toEntity(transactionHistory));
    }

    @Transactional
    protected void handleUsedVoucher(PaymentRequestDTO paymentRequestDTO) {
        if (paymentRequestDTO.getVoucherCode() != null && !paymentRequestDTO.getVoucherCode().isEmpty()) {
            var usedVoucher = voucherService.validVoucherByUser(paymentRequestDTO.getVoucherCode());
            usedVoucher.setQuantity(usedVoucher.getQuantity() - 1);
            var savedVoucher = voucherRepository.save(voucherMapper.toEntity(usedVoucher));

            var voucherUsed = new Voucher_use();
            voucherUsed.setUseDate(TimeUtils.toLocalDate(paymentRequestDTO.getOrderDate()));
            voucherUsed.setSysIdUser(paymentRequestDTO.getUserId());
            voucherUsed.setSysIdVoucherUseDetail(savedVoucher);
            voucherUsedRepository.save(voucherUsed);
        }
    }

    @Transactional
    protected void handleOrders(PaymentRequestDTO paymentRequestDTO) {
        if (paymentRequestDTO.getOrders() != null) {
            List<String> licenseKeys = new ArrayList<>();
            for (var order : paymentRequestDTO.getOrders()) {
//                log.info("order: {}", order);
                var game = gameService.findBySlug(order.getSlug());
                order.setSysIdUser(paymentRequestDTO.getUserId());
                order.setOrderCode(paymentRequestDTO.getOrderCode());
                order.setOrderDate(paymentRequestDTO.getOrderDate());
                order.setPaymentStatus(true);
//                order.setQuantityPurchased();
                order.setTotalPayment(paymentRequestDTO.getTotalPayment());
                order.setSysIdProduct(game.getSysIdGame());
//                log.info("order2: {}", order);
                gameService.updateGameQuantityAndSold(game.getSysIdGame(), order.getQuantity());
                ordersRepository.save(ordersMapper.toEntity(order));

                var ownedGame = OwnedGameDTO.builder()
                        .gameId(game.getSysIdGame())
                        .userId(paymentRequestDTO.getUserId())
                        .ownedDate(paymentRequestDTO.getOrderDate())
                        .build();
                ownedGameRepository.save(ownedGameMapper.toEntity(ownedGame));
                for (int i = 0; i < order.getQuantity(); i++) {
                    licenseKeys.add(game.getGameName() + "!@#%&" + LicenseKeyUtils.generateLicenseKey());
                }
            }
            sendLicenseKeysByEmail(usersService.getCurrentUser().getEmail(), licenseKeys);
        } else {
            throw new CustomException("Orders is empty", HttpStatus.BAD_REQUEST);
        }
    }

    private void sendLicenseKeysByEmail(String email, List<String> licenseKeys) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your License Keys");

            StringBuilder emailContent = new StringBuilder("Here are your license keys:<br>");
            for (String licenseKey : licenseKeys) {
                String[] parts = licenseKey.split("!@#%&");
                emailContent.append("<b>").append(parts[0]).append("</b>: ").append(parts[1]).append("<br>");
            }

            helper.setText(emailContent.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomException("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}