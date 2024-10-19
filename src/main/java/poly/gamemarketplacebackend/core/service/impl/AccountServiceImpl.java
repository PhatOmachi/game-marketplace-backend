package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.entity.Account;
import poly.gamemarketplacebackend.core.mapper.AccountMapper;
import poly.gamemarketplacebackend.core.repository.AccountRepository;
import poly.gamemarketplacebackend.core.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Override
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO getAccountById(int id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDTO)
                .orElse(null);
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        account = accountRepository.save(account);
        return accountMapper.toDTO(account);
    }

    @Override
    public AccountDTO updateAccount(int id, AccountDTO accountDTO) {
        if (accountRepository.existsById(id)) {
            Account account = accountMapper.toEntity(accountDTO);
            account.setId(id);
            account = accountRepository.save(account);
            return accountMapper.toDTO(account);
        }
        return null;
    }

    @Override
    public void deleteAccount(int id) {
        accountRepository.deleteById(id);
    }
}