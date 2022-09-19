package com.mindhub.homebanking.Controllers;


import com.mindhub.homebanking.DTO.CardDTO;
import com.mindhub.homebanking.Services.CardService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
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
public class CardControllers {
    @Autowired
    CardService cardService;
    @Autowired
    ClientService clientService;

    @GetMapping("/cards")
    public List<CardDTO> getCards(){
        return cardService.getAllCards().stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/cards/{id}")
    public CardDTO getCard(@PathVariable Long id){
        return new CardDTO(cardService.getCardById(id));
    }

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createCard(@RequestParam CardColor color, @RequestParam CardType type, Authentication authentication) {

        int random1 = getRandomNumber(1000, 9999);
        int random2 = getRandomNumber(1000, 9999);
        int random3 = getRandomNumber(1000, 9999);
        int random4 = getRandomNumber(1000, 9999);
        int cvv = getRandomNumber(100, 999);

        Client client = clientService.getClientByEmail(authentication.getName());

        if(client != null){
            if(client.getCards().stream().filter(card -> card.getType() == type).collect(Collectors.toList()).size() < 3){
                Card card = new Card(client, type, color, random1 + " " + random2 + " " + random3 + " " + random4, cvv, LocalDateTime.now(), LocalDateTime.now().plusYears(5));
                cardService.saveCard(card);

                return new ResponseEntity<>(HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>("Max cards", HttpStatus.FORBIDDEN);
            }

        } else {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/cards/state")
    public ResponseEntity<Object> changeIsActive(Authentication authentication,@RequestParam String number, @RequestParam String state){
        Client client = clientService.getClientByEmail(authentication.getName());
        Card card = cardService.getCardByNumber(number);
        if (client == null){
            return new ResponseEntity<>("Client doesn´t exist", HttpStatus.FORBIDDEN);
        }

        if (card == null){
            return new ResponseEntity<>("Card doesn´t exist", HttpStatus.FORBIDDEN);
        }
        if(state.equals("deactivate")){
            card.setActive(false);
            cardService.saveCard(card);
        } else{
            card.setActive(true);
            cardService.saveCard(card);
        }
        return new ResponseEntity<>("Change isActive property success", HttpStatus.ACCEPTED);
    }
}