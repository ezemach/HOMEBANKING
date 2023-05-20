package com.mindhub.homebanking.service.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImplement implements AccountService {

    @Autowired
    private AccountRepository repoAccount;


    @Override
    public List<AccountDTO> getAccount() {
        return repoAccount.findAll()
                .stream()
                .map(account -> new AccountDTO(account))
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO getAccount(Long id) {
        return repoAccount.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }
    @Override
    public void saveAccount(Account account) {
        repoAccount.save(account);
    }

    @Override
    public Account findByNumber(String number) {
        return repoAccount.findByNumber(number);
    }

    @Override
    public Account findById(Long id) {
        {
            return repoAccount.findById(id).orElse(null);
        }
    }
}
