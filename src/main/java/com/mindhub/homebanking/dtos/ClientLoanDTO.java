package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {

    private long id;
    private long id_Loan;
    private String name;
    private double amount;
    private int payments;

    public ClientLoanDTO (ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.id_Loan = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
    }


    public long getId() {
        return id;
    }

    public long getId_Loan() {
        return id_Loan;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }
}
