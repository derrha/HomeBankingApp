package com.mindhub.homebanking.Controllers;

import com.mindhub.homebanking.DTO.CardDTO;
import com.mindhub.homebanking.DTO.LoanApplicationDTO;
import com.mindhub.homebanking.DTO.LoanDTO;
import com.mindhub.homebanking.Services.*;
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
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class LoanControllers {
    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;



    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanService.getAllLoans().stream().map(LoanDTO::new).collect(Collectors.toList());
    }
    @GetMapping("/loans/{id}")
    public LoanDTO getLoan(@PathVariable Long id){
        return new LoanDTO(loanService.getLoanById(id));
    }



    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplication){
        if (loanApplication.getPayments() != 0 || loanApplication.getPayments() != 0 ) {
            Loan loan = loanService.getLoanById(loanApplication.getId());
            if (loan != null) {
                if (loanApplication.getAmount() <= loan.getMaxAmount()) {
                    if (loan.getPayments().contains(loanApplication.getPayments())) {
                        Account destinationAccount = accountService.getAccountByNumber(loanApplication.getDestinationNumberAccount());
                        if (destinationAccount != null) {
                            Client client = clientService.getClientByEmail(authentication.getName());
                            ClientLoan clientLoan = new ClientLoan((loanApplication.getAmount() * 0.2) + loanApplication.getAmount(), loanApplication.getPayments(), client, loan);

                            List<String> loansName = client.getClientLoans().stream().map(clientLoan1 -> clientLoan1.getLoan().getName()).collect(Collectors.toList());

                            if(loansName.contains(clientLoan.getLoan().getName())){
                                return new ResponseEntity<>("Already apply for this Loan", HttpStatus.FORBIDDEN);
                            }
                            if (client.getAccounts().contains(destinationAccount)) {
                                clientLoanService.saveClientLoan(clientLoan);
                                Transaction transaction = new Transaction(loan.getName() + " Prestamo aprobado", loanApplication.getAmount() , LocalDateTime.now(), destinationAccount, TransactionType.CREDIT);
                                transactionService.saveTransaction(transaction);
                                destinationAccount.setBalance(destinationAccount.getBalance() + loanApplication.getAmount());

                                return new ResponseEntity<>("Approved loan", HttpStatus.CREATED);

                            } else {
                                return new ResponseEntity<>("The account does not belong to the client", HttpStatus.FORBIDDEN);
                            }
                        } else {
                            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>("Wrong Payment", HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>("Excess amount", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);

        }
    }

}
