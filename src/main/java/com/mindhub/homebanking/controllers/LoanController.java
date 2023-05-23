package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.*;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.service.*;
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
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientLoanService clientLoanService;


    @Transactional
    @RequestMapping(path = "/api/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> addLoan(Authentication authentication,
                                           @RequestBody LoanApplicationDTO loanApplicationDTO) {

        Client client = clientService.findByEmail(authentication.getName());
        Account account = accountService.findByNumber(loanApplicationDTO.getNumber().toUpperCase());
        Loan loan = loanService.findById(loanApplicationDTO.getId_Loan());

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

        // Obtener el monto de la solicitud de préstamo
        double interesSeleccionado = loan.getInterests();

        // Calcular interes del monto solicitado
        double interes = monto * interesSeleccionado;

        // Actualizar el monto de la solicitud de préstamo
        double montoConInteres = interes + monto;

        // Crear una instancia de Loan con los datos actualizados
        ClientLoan SolicitudClientLoan = new ClientLoan ( montoConInteres, loanApplicationDTO.getPayments(), loanApplicationDTO.getNumber());
        clientLoanService.saveClientLoan(SolicitudClientLoan);

        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, monto, loan.getName() + " loan approved", LocalDateTime.now(),account.getBalance() + monto);
        transactionService.saveTransaction(creditTransaction);

        account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
        account.addTransaction(creditTransaction);

        loan.addClientLoan(SolicitudClientLoan);
        client.addClientLoan(SolicitudClientLoan);
        clientService.save(client);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/api/loans")
    public List<LoanDTO> getLoanDTO() {
        return loanService.getLoanDTO();

    }

    @PostMapping(path = "/api/loans/create")
    public ResponseEntity<Object> addNewLoan(Authentication authentication,
                                             @RequestBody LoanApplicationDTO2 loanApplicationDTO2) {

        Client client = clientService.findByEmail(authentication.getName());

        //NAME
        if (loanApplicationDTO2.getName().isEmpty()) {
            return new ResponseEntity<>("Put the name of Loan", HttpStatus.FORBIDDEN);
        }
        //PAYMENTS
        if (loanApplicationDTO2.getPayments().isEmpty()) {
            return new ResponseEntity<>("please put a valid number of payments", HttpStatus.FORBIDDEN);
        }
        //AMOUNT
        if (loanApplicationDTO2.getMaxAmount() < 5000) {
            return new ResponseEntity<>("Enter an amount bigger than 5000", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO2.getMaxAmount() > 1500000) {
            return new ResponseEntity<>("you have exceeded the possible amount available of 1.5 million", HttpStatus.FORBIDDEN);
        }
        //LOAN
        if (loanService.getLoanDTO().stream().anyMatch(loanDTO -> loanDTO.getName().equalsIgnoreCase(loanApplicationDTO2.getName()))) {
            return new ResponseEntity<>("the loan already exist!", HttpStatus.FORBIDDEN);
        }
        //in
        if (loanApplicationDTO2.getInterests() > 4) {
            return new ResponseEntity<>("The interest exceed the 400% rate", HttpStatus.FORBIDDEN);
        }


        Loan loanAdd = new Loan( loanApplicationDTO2.getName().toUpperCase(), loanApplicationDTO2.getMaxAmount(), loanApplicationDTO2.getPayments(), loanApplicationDTO2.getInterests());
        loanService.save(loanAdd);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PostMapping("/api/loans/payments")
    public ResponseEntity<Object> makePayment(
            Authentication authentication,
            @RequestBody LoanPaymentDTO loanPaymentDTO
    ) {
        Client client = clientService.findByEmail(authentication.getName());
        Account account = accountService.findByNumber(loanPaymentDTO.getNumber().toUpperCase());
        ClientLoan clientLoan = clientLoanService.findById(loanPaymentDTO.getId());

        // Obtener el préstamo por su ID

        if (clientLoan == null) {
            return new ResponseEntity<>("Préstamo no encontrado", HttpStatus.NOT_FOUND);
        }

        // Obtener la cuenta por su ID

        if (account == null) {
            return new ResponseEntity<>("Cuenta no encontrada", HttpStatus.NOT_FOUND);
        }


        // monto de la cuota a pagar
        double montoCuota = clientLoan.getAmount() / clientLoan.getPayments() ;
        // Restar del monto 1 cuota
        double newAmount = clientLoan.getAmount() - montoCuota;
        // Restar 1 al número de cuotas pendientes
        int newPayments = clientLoan.getPayments() - (clientLoan.getPayments() - 1);



        //Nueva instancia de Transaction
        Transaction paymentTransaction = new Transaction(TransactionType.DEBIT,montoCuota," " + newPayments +"/"+clientLoan.getPayments() +" "+ clientLoan.getName()+" Loan",LocalDateTime.now(),account.getBalance() - montoCuota);
        transactionService.saveTransaction(paymentTransaction);

        // Actualiza el préstamo con los nuevos valores
        clientLoan.setAmount(clientLoan.getAmount() - montoCuota);
        clientLoan.setPayments(clientLoan.getPayments() - 1);
        clientLoanService.saveClientLoan(clientLoan);

        account.setBalance(account.getBalance() - montoCuota);
        account.addTransaction(paymentTransaction);
        accountService.saveAccount(account);

        client.addClientLoan(clientLoan);
        clientService.save(client);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    }
