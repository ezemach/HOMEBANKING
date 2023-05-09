package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository repoAccount;
    @Autowired
    private ClientRepository clientRepository;



    @RequestMapping("/api/accounts")
    public List<AccountDTO> getAccount() {

        return repoAccount.findAll()
                .stream()
                .map(account -> new AccountDTO(account))
                .collect(Collectors.toList());
    }
    @RequestMapping("/api/accounts/{id}")

    public AccountDTO getAccount(@PathVariable Long id){
        return repoAccount.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }

    @RequestMapping(path = "/api/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createNewAccount(Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        if(client.getAccounts().size() < 3){
            String accountNumber;
            do {
                int randomNumber = (int) (Math.random() * 100000000);
                accountNumber = "VIN" + String.format("%08d", randomNumber);
            } while (repoAccount.findByNumber(accountNumber) != null);
            Account newAccount = new Account(accountNumber, LocalDateTime.now(),0);
            client.addAccount(newAccount);
            repoAccount.save(newAccount);
        } else{
            return new ResponseEntity<>("you can`t have more than 3 accounts", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }




}
