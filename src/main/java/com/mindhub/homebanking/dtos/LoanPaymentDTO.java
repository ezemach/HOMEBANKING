package com.mindhub.homebanking.dtos;

public class LoanPaymentDTO {

    private long id;
    private int payments;
    private String number;

    public LoanPaymentDTO() {}

    public LoanPaymentDTO( int payments, String number, long id) {
        this.payments = payments;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public int getPayments() {
        return payments;
    }

    public String getNumber() {
        return number;
    }
}
