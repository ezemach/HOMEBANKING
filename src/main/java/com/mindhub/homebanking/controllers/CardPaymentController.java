package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardPaymentDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class CardPaymentController {
    @Autowired
    private CardService cardService;
    @Autowired
    private TransactionService transactionService;
    @Transactional
    @PostMapping("api/card/payment")
    public ResponseEntity<Object> paymentWithCard(@RequestBody CardPaymentDTO cardPaymentDTO){
        int cvv = cardPaymentDTO.getCvv();
        double amount = cardPaymentDTO.getAmount();
        String number = cardPaymentDTO.getNumber();
        String description = cardPaymentDTO.getDescription();

        if (cvv <= 0){
            return new ResponseEntity<>("Invalid CVV (must be > 1)", HttpStatus.FORBIDDEN);
        }
        if (cvv > 999 ){
            return new ResponseEntity<>("Invalid CVV (Only 3 digits)", HttpStatus.FORBIDDEN);
        }
        if (amount < 1){
            return new ResponseEntity<>("Invalid Amount (must be > 1)", HttpStatus.FORBIDDEN);
        }
        if (number.isBlank()){
            return new ResponseEntity<>("Number cannot be empty", HttpStatus.FORBIDDEN);
        }
        if (number.length() < 19 || number.length() > 19){
            return new ResponseEntity<>("Invalid card Number", HttpStatus.FORBIDDEN);
        }
        if (description.isBlank()){
            return new ResponseEntity<>("Description cannot be empty", HttpStatus.FORBIDDEN);
        }

        Card card = cardService.findByNumber(cardPaymentDTO.getNumber());
        if (card == null){
            return new ResponseEntity<>("Card not found", HttpStatus.FORBIDDEN);
        }

        Client client = card.getClient();
        List<Account> accounts = client.getAccounts().stream().filter(account -> account.getBalance() >= amount).collect(Collectors.toList());
        Account account = accounts.stream().findFirst().orElse(null);

        if (card.getThruDate().isBefore(LocalDate.now()) || card.getThruDate().equals(LocalDateTime.now())){
            return new ResponseEntity<>("Expired Card", HttpStatus.FORBIDDEN);
        }
        if (account == null){
            return new ResponseEntity<>("Insufficient amount", HttpStatus.FORBIDDEN);
        }
        if (!card.isActive()){
            return new ResponseEntity<>("Inactive Card", HttpStatus.FORBIDDEN);
        }
        if (card.getCvv() != cvv){
            return new ResponseEntity<>("Invalid CVV", HttpStatus.FORBIDDEN);
        }

        double accountBalance = account.getBalance();
        account.setBalance(accountBalance-amount);

        Transaction newTransaction = new Transaction(TransactionType.DEBIT,amount,account.getNumber()+" "+description,LocalDateTime.now(),accountBalance-amount);
        account.addTransaction(newTransaction);
        transactionService.saveTransaction(newTransaction);

        return new ResponseEntity<>("Payment Accepted", (HttpStatus.CREATED));
    }
}
