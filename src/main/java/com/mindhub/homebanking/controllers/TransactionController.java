package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @Transactional
    @RequestMapping(path = "/api/clients/current/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> NewTransaction(Authentication authentication,
                                                 @RequestParam double amount,
                                                 @RequestParam String description,
                                                 @RequestParam String numberOrigin,
                                                 @RequestParam String numberDestiny) {

        Client client = clientService.findByEmail(authentication.getName());
        Account accountOrigin = accountService.findByNumber(numberOrigin.toUpperCase());
        Account accountDestiny = accountService.findByNumber(numberDestiny.toUpperCase());
        Account balance = accountService.findByNumber(numberOrigin.toUpperCase());
        Account balance2 = accountService.findByNumber(numberOrigin.toUpperCase());

        if (description.isBlank() || numberOrigin.isEmpty() || numberDestiny.isEmpty() || amount < 1.0) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (accountOrigin == accountDestiny) {
            return new ResponseEntity<>("The accounts number are same", HttpStatus.FORBIDDEN);
        }
        if (accountOrigin == null) {
            return new ResponseEntity<>("Origin Account doesn´t exist", HttpStatus.FORBIDDEN);
        }
        if (accountDestiny == null) {
            return new ResponseEntity<>("Destiny Account doesn´t exist", HttpStatus.FORBIDDEN);
        }
        if (accountOrigin.getClient().getEmail() != authentication.getName()) {
            return new ResponseEntity<>("Origin Account is not yours", HttpStatus.FORBIDDEN);
        }
        if (accountOrigin.getBalance() < amount) {
            return new ResponseEntity<>("you don´t have enough money", HttpStatus.FORBIDDEN);
        }

        accountOrigin.setBalance(accountOrigin.getBalance() - amount);
        accountDestiny.setBalance(accountDestiny.getBalance() + amount);

//        balance.setBalance(balance.getBalance() - amount);
//        balance2.setBalance(balance.getBalance() + amount);


        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, amount, description + " " + accountOrigin.getNumber(), LocalDateTime.now(), accountOrigin.getBalance()) ;
        accountOrigin.addTransaction(transactionDebit);

        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, description + " " + accountDestiny.getNumber(), LocalDateTime.now(),accountDestiny.getBalance());
        accountDestiny.addTransaction(transactionCredit);
        transactionService.saveTransaction(transactionDebit);
        transactionService.saveTransaction(transactionCredit);


        return new ResponseEntity<>(HttpStatus.CREATED);
    }



}
