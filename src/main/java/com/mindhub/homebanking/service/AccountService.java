package com.mindhub.homebanking.service;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface AccountService {

    List<AccountDTO> getAccount();

    AccountDTO getAccount(Long id);

    void saveAccount (Account account);

    Account findByNumber(String number);

    Account findById(Long id);
}
