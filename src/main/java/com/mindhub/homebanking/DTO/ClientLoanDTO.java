package com.mindhub.homebanking.DTO;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.models.Transaction;

public class ClientLoanDTO {
    private long id;
    private Double amount;
    private  Integer payment;

    private long loanId;

    private String name;


    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.amount = clientLoan.getAmount();
        this.loanId = clientLoan.getLoan().getId();
        this.payment = clientLoan.getPayments();
        this.name = clientLoan.getLoan().getName();
    }

    public long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayment() {
        return payment;
    }

    public long getLoanId() {
        return loanId;
    }

    public String getName() {
        return name;
    }
}
