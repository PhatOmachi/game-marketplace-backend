package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.dto.PaymentRequestDTO;
import poly.gamemarketplacebackend.core.dto.TransactionHistoryDTO;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;
import poly.gamemarketplacebackend.core.entity.Voucher_use;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.OrdersMapper;
import poly.gamemarketplacebackend.core.mapper.TransactionHistoryMapper;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.repository.*;
import poly.gamemarketplacebackend.core.service.*;
import poly.gamemarketplacebackend.core.util.TimeUtils;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {
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

    @Transactional
    @Override
    public void handlePayment(PaymentRequestDTO paymentRequestDTO) {
        handleUserBalance(paymentRequestDTO);
        handleUsedVoucher(paymentRequestDTO);
        handleOrders(paymentRequestDTO);
    }

    private void handleUserBalance(PaymentRequestDTO paymentRequestDTO) {
        var currentUser = usersService.getCurrentUser();
        if (currentUser.getSysIdUser() != paymentRequestDTO.getUserId()) {
            throw new CustomException("Invalid user commit payment", HttpStatus.UNAUTHORIZED);
        }
        var userBalance = Float.parseFloat(currentUser.getBalance());
        userBalance -= paymentRequestDTO.getTotalPayment();
        if (userBalance < 0) {
            throw new CustomException("Not enough balance", HttpStatus.BAD_REQUEST);
        }
        usersRepository.updateUsersByUsername(String.valueOf(userBalance), currentUser.getUsername());
        handleTransactionHistory(paymentRequestDTO);
    }

    private void handleTransactionHistory(PaymentRequestDTO paymentRequestDTO) {
        var transactionHistory = new TransactionHistoryDTO();
        transactionHistory.setAmount(paymentRequestDTO.getTotalPayment());
        transactionHistory.setDescription(paymentRequestDTO.getOrderCode());
        transactionHistory.setPaymentTime(paymentRequestDTO.getOrderDate());
        transactionHistory.setStatus(true);
        transactionHistory.setUsername(paymentRequestDTO.getUsername());
        transactionHistoryService.save(transactionHistoryMapper.toEntity(transactionHistory));
    }

    @Transactional
    protected void handleUsedVoucher(PaymentRequestDTO paymentRequestDTO) {
        if (paymentRequestDTO.getVoucherCode() != null) {
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

    private void handleOrders(PaymentRequestDTO paymentRequestDTO) {
        if (paymentRequestDTO.getOrders() != null) {
            for (var order : paymentRequestDTO.getOrders()) {
                order.setSysIdUser(paymentRequestDTO.getUserId());
                order.setOrderCode(paymentRequestDTO.getOrderCode());
                order.setOrderDate(paymentRequestDTO.getOrderDate());
                order.setPaymentStatus(true);
                order.setTotalPayment(paymentRequestDTO.getTotalPayment());
                order.setSysIdProduct(gameService.findBySlug(order.getSlug()).getSysIdGame());
                ordersRepository.save(ordersMapper.toEntity(order));
            }
        } else {
            throw new CustomException("Orders is empty", HttpStatus.BAD_REQUEST);
        }
    }
}