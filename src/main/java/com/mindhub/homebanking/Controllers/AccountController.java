package com.mindhub.homebanking.Controllers;


import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.DTO.ClientDTO;
import com.mindhub.homebanking.Services.AccountService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.Utils.Util.getRandomNumber;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    ClientService clientService;
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAllAccounts().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return new AccountDTO(accountService.getAccountById(id));
    };

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam AccountType accountType){

        int random = getRandomNumber(0,99999999);

        Client client = clientService.getClientByEmail(authentication.getName());
        if (client != null){
            if (client.getAccount().stream().filter(Account::getActive).toArray().length < 3) {
                Account account = new Account("VIN" + random, LocalDateTime.now(), 0.00, client, accountType);
                accountService.saveAccount(account);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("Max accounts", HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/accounts/delete")
    public ResponseEntity<Object> disableAccount(Authentication authentication,@RequestParam String accountNumber){
        Client client = clientService.getClientByEmail(authentication.getName());
        Account account = accountService.getAccountByNumber(accountNumber);
        if (client == null){
            return new ResponseEntity<>("1", HttpStatus.FORBIDDEN);
        }
        if (account == null){
            return new ResponseEntity<>("2", HttpStatus.FORBIDDEN);
        }
        if (account.getBalance() != 0 ){
            return new ResponseEntity<>("3", HttpStatus.FORBIDDEN);
        }
        account.setActive(false);
        accountService.saveAccount(account);
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

}
