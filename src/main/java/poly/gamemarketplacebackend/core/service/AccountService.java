package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.AccountDTO;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAllAccounts();
    AccountDTO getAccountById(int id);
    AccountDTO createAccount(AccountDTO accountDTO);
    AccountDTO updateAccount(int id, AccountDTO accountDTO);
    void deleteAccount(int id);
}