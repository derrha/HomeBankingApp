package com.mindhub.homebanking.Controllers;

import com.mindhub.homebanking.Services.AccountService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.Services.PDFService;
import com.mindhub.homebanking.Services.TransactionService;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.hibernate.criterion.IdentifierEqExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    ClientService clientService;

    @Autowired
    PDFService pdfService;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction (@RequestParam double amount , @RequestParam String description, @RequestParam String sourceNumber, @RequestParam String destinationNumber, Authentication authentication) {
        if (sourceNumber.isEmpty() || destinationNumber.isEmpty() || amount == 0.0) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        } else {
            if (!sourceNumber.equals(destinationNumber)) {
                Account sourceAccount = accountService.getAccountByNumber(sourceNumber);
                if (sourceAccount != null){
                    Client client = clientService.getClientByEmail(authentication.getName());
                    if (client != null) {
                        if (client.getAccounts().contains(sourceAccount)){
                            Account destinationAccount = accountService.getAccountByNumber(destinationNumber);
                            if (destinationAccount != null) {
                                if (sourceAccount.getBalance() >= amount) {
                                    if(amount > 0){
                                        Transaction transaction1 = new Transaction(description + "Transferencia a: " + destinationNumber, amount, LocalDateTime.now(), sourceAccount, TransactionType.DEBIT);
                                        Transaction transaction2 = new Transaction(description + "Transferencia de: " + sourceNumber, amount, LocalDateTime.now(), destinationAccount, TransactionType.CREDIT);
                                        transactionService.saveTransaction(transaction1);
                                        transactionService.saveTransaction(transaction2);
                                        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
                                        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
                                        return new ResponseEntity<>("Transaction Created", HttpStatus.CREATED);
                                    }else {
                                        return new ResponseEntity<>("Negative amount", HttpStatus.FORBIDDEN);
                                    }
                                } else {
                                    return new ResponseEntity<>("Not enough amount", HttpStatus.FORBIDDEN);
                                }
                            }else {
                                return new ResponseEntity<>("Missing destination", HttpStatus.FORBIDDEN);
                            }
                        }else {
                            return new ResponseEntity<>("This account do not belong to this client", HttpStatus.FORBIDDEN);
                        }
                    }else {
                        return new ResponseEntity<>("Missing client", HttpStatus.FORBIDDEN);
                    }
                }else {
                    return new ResponseEntity<>("Missing source account", HttpStatus.FORBIDDEN);
                }
            }else {
                return new ResponseEntity<>("Select another account", HttpStatus.FORBIDDEN);
            }
        }
    }
    @GetMapping("/transactions/{number}")
    public Client getDestinationName(@PathVariable String number){
        Client client = clientService.getClientByAccountsNumber(number);
        return new Client(client.getClientLastName(), client.getClientName());
    }

    @GetMapping("/transactions/current")
    public ResponseEntity<Object> getTransactionsCurrent(HttpServletResponse response, Authentication authentication, @RequestParam String accountNumber, @RequestParam(required = false) String start, @RequestParam(required = false) String end){
        Client client = clientService.getClientByEmail(authentication.getName());
        Account account = accountService.getAccountByNumber(accountNumber);
        List<Transaction> transactions;
        if (client.getAccounts().contains(account)){
            response.setContentType("application/pdf");
            String headerKey = "Content-Disposition";
            String headerValue = "inline";
            response.setHeader(headerKey, headerValue);
            if (!(start.isEmpty() || end.isEmpty())){
                LocalDateTime startDate = LocalDateTime.parse(start);
                LocalDateTime endDate = LocalDateTime.parse(end);
                transactions = transactionService.getTransactionsByAccountAndDate(account,startDate,endDate);

            } else {
                transactions = transactionService.getAllTransactionsByAccount(account);
            }
            pdfService.generatePDF(response, transactions, account);
            return  new ResponseEntity<>("", HttpStatus.ACCEPTED);
        } else {
            return null;
        }
    }
}
