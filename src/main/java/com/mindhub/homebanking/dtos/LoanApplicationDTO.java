package com.mindhub.homebanking.dtos;


public class LoanApplicationDTO {

    private long id_Loan;
    private double amount;
    private int payments;
    private String number;

    public LoanApplicationDTO () {}

    public LoanApplicationDTO(long id_Loan, double amount, int payments, String number) {
        this.id_Loan = id_Loan;
        this.amount = amount;
        this.payments = payments;
        this.number = number;
    }

    public long getId_Loan() {
        return id_Loan;
    }

    public void setId_Loan(long id_Loan) {
        this.id_Loan = id_Loan;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
