package com.mindhub.homebanking.Controllers;

import com.mindhub.homebanking.DTO.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.Utils.Util.getRandomNumber;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(Collectors.toList());
    };

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository.findById(id).map(ClientDTO::new)
                .orElse(null);
    };

    @GetMapping("/clients/current")

    public ClientDTO  getAll(Authentication authentication) {

        Client client = clientRepository.findByClientMail(authentication.getName());
        return new ClientDTO(client);
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)

    public ResponseEntity<Object> register(

            @RequestParam String clientName, @RequestParam String clientLastName,
            @RequestParam String clientMail, @RequestParam String clientPassword) {

        if (clientName.isEmpty() || clientLastName.isEmpty() || clientMail.isEmpty() || clientPassword.isEmpty()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (clientRepository.findByClientMail(clientMail) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        int random = getRandomNumber(0, 99999999);

        Client client = new Client(clientName, clientLastName, clientMail, passwordEncoder.encode(clientPassword));
        clientRepository.save(client);

        Account account = new Account("VIN" + random, LocalDateTime.now(), 0.00, client, AccountType.SAVING);
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }
}
