package com.mindhub.homebanking.DTO;

import com.mindhub.homebanking.models.Loan;

import java.util.List;

public class LoanDTO {
    private long id;
    private String name;
    private List<Integer> payments;
    private double maxAmount;
    private Double interest;
    public LoanDTO(Loan loan){
        this.id = loan.getId();
        this.payments = loan.getPayments();
        this.maxAmount = loan.getMaxAmount();
        this.name = loan.getName();
        this.interest = loan.getInterest();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public Double getInterest() {
        return interest;
    }
}
