package com.mindhub.homebanking.dtos;

import java.util.List;

public class LoanApplicationDTO2 {

    private String name;
    private double maxAmount;
    private List<Integer> payments;
    private double interests;

    public LoanApplicationDTO2() {}

    public LoanApplicationDTO2(String name, double maxAmount, List<Integer> payments, double interests) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
        this.interests = interests;
    }


    public String getName() {
        return name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public double getInterests() {
        return interests;
    }
}
