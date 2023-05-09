package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class LoanController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;



    @Transactional
    @RequestMapping(path = "/api/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> addLoan(Authentication authentication,
                                           @RequestBody LoanApplicationDTO loanApplicationDTO) {

        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findByNumber(loanApplicationDTO.getNumber().toUpperCase());
        Loan loan = loanRepository.findById(loanApplicationDTO.getId_Loan());

        if (loan == null) {
            return new ResponseEntity<>("This loan doesn´t exist", HttpStatus.FORBIDDEN);
        }
        //PAYMENTS
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("please put a valid number of payments", HttpStatus.FORBIDDEN);
        }
        //AMOUNT
        if (Double.isNaN(loanApplicationDTO.getAmount()) || loanApplicationDTO.getAmount() < 1) {
            return new ResponseEntity<>("Enter an amount bigger than 0", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("you have exceeded the possible amount", HttpStatus.FORBIDDEN);
        }
        //ACCOUNT
        if (loanApplicationDTO.getNumber().isBlank()) {
            return new ResponseEntity<>("Destiny Account is empty", HttpStatus.FORBIDDEN);
        }
        if (account == null) {
            return new ResponseEntity<>("This account doesn´t exist", HttpStatus.FORBIDDEN);
        }
        if (!client.getAccounts().contains(account)){
            return new ResponseEntity<>("This account doesn´t belong to aclient", HttpStatus.FORBIDDEN);
        }


        // Obtener el monto de la solicitud de préstamo
        double monto = loanApplicationDTO.getAmount();
        // Calcular el 20% del monto solicitado
        double veintePorciento = monto * 0.2;
        // Actualizar el monto de la solicitud de préstamo sumando el 20%
        double montoConInteres = monto + veintePorciento;

        // Crear una instancia de Loan con los datos actualizados
        ClientLoan SolicitudClientLoan = new ClientLoan ( montoConInteres, loanApplicationDTO.getPayments(), loanApplicationDTO.getNumber());
        clientLoanRepository.save(SolicitudClientLoan);

        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, monto, loan.getName() + " loan approved", LocalDateTime.now());
        transactionRepository.save(creditTransaction);

        account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
        account.addTransaction(creditTransaction);

        loan.addClientLoan(SolicitudClientLoan);
        client.addClientLoan(SolicitudClientLoan);
        clientRepository.save(client);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/api/loans")
    public List<LoanDTO> getLoanDTO() {
        return loanRepository.findAll()
                .stream()
                .map(LoanDTO::new)
                .collect(toList());
    }

}
