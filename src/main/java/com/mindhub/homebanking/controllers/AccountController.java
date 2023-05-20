package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;



    @RequestMapping("/api/accounts")
    public List<AccountDTO> getAccount() {
        return accountService.getAccount();
    }
    @RequestMapping("/api/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountService.getAccount(id);
    }

    @RequestMapping(path = "/api/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createNewAccount(Authentication authentication,
                                                     @RequestParam String type) {
        Client client = clientService.findByEmail(authentication.getName());

        List<Account> activeAccounts = client.getAccounts().stream()
                .filter(account -> account.isActive() &&
                        account.getType() == AccountType.valueOf(type))
                .collect(Collectors.toList());


             if (activeAccounts.size() < 3){
            String accountNumber;
            do {
                int randomNumber = (int) (Math.random() * 100000000);
                accountNumber = "VIN" + String.format("%08d", randomNumber);
            } while (accountService.findByNumber(accountNumber) != null);
            Account newAccount = new Account(accountNumber, LocalDateTime.now(),0, true, AccountType.valueOf(type));
            client.addAccount(newAccount);
            accountService.saveAccount(newAccount);
        } else{
            return new ResponseEntity<>("you can`t have more than 3 accounts", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PutMapping("/api/accounts/{id}")
    public ResponseEntity<Object> deleteAccount(Authentication authentication, @PathVariable Long id){
          Account account = accountService.findById(id);
          Client client = clientService.findByEmail(authentication.getName());
        if (account == null){
            return new ResponseEntity<>("Account is not found", HttpStatus.NOT_FOUND);
        }
        if (client.getAccounts().stream().noneMatch(account1 -> account1.getId() == id)){
            return new ResponseEntity<>("Account is not yours", HttpStatus.FORBIDDEN);
        }
        if (account.getBalance() != 0){
            return new ResponseEntity<>("you can not delete, you have money to transfer or a debt to pay", HttpStatus.FORBIDDEN);
        }

        account.setActive(false);
        accountService.saveAccount(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }






}
