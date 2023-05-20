package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
public class CardController {


    @Autowired
    private CardService cardService;

    @Autowired
    private ClientService clientService;


    @RequestMapping(path = "/api/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createNewCard(Authentication authentication,
                                                   @RequestParam String type,
                                                   @RequestParam String color ) {

        Client client = clientService.findByEmail(authentication.getName());

//        Set <Card> cards = client.getCards().stream().filter(card -> card.getType() ==  card.getType() && card.isActive()).collect(Collectors.toSet());

        int debitCant = 0;
        int creditCant= 0;
        Set<String> debits = new HashSet<>();
        Set<String> credits = new HashSet<>();

        for(Card card : client.getCards()){
            if(card.getType().equals(CardType.CREDIT) && card.isActive()){
                creditCant++;
                credits.add(card.getColor().name());
            } else if (card.getType().equals(CardType.DEBIT) && card.isActive()){
                debitCant++;
                debits.add(card.getColor().name());
            }
//            if (card.getType().equals(CardType.valueOf(type)) && card.getColor().equals(CardColor.valueOf(color))) {
//                return new ResponseEntity<>("already have 0" + type.toLowerCase() + "card whit this color", HttpStatus.FORBIDDEN);
//            }
        }




        if(type.equals("CREDIT") && creditCant < 3 && !credits.contains(color)){
            Card newCard = new Card (CardType.CREDIT, CardColor.valueOf(color),client.getFirstName() +" "+ client.getLastName(), LocalDate.now(), LocalDate.now().plusYears(5), randomNumber(), Integer.parseInt(randomCvv()), true);
            client.addCard(newCard);
            cardService.save(newCard);}
        else if (type.equals("DEBIT") && debitCant < 3 && !debits.contains(color)) {
            Card newCard = new Card (CardType.DEBIT, CardColor.valueOf(color),client.getFirstName() +" "+ client.getLastName(), LocalDate.now(), LocalDate.now().plusYears(5), randomNumber(), Integer.parseInt(randomCvv()), true);
            client.addCard(newCard);
            cardService.save(newCard);
        }
        else {
        return new ResponseEntity<>("you already have that of card!", HttpStatus.FORBIDDEN);
    }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    private static String randomCvv() {
        int num = (int) (Math.random() * 1000);
        return String.format("%03d", num);
    }
    private static String randomNumber() {
        Random randomNum = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int num = randomNum.nextInt(10000);
            sb.append(String.format("%04d", num));
            if (i < 3) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    @PostMapping("/api/cards/{id}")
    public ResponseEntity<Object> deleteCard(Authentication authentication, @PathVariable Long id){
        Card card = cardService.findById(id);
        Client client = clientService.findByEmail(authentication.getName());
        if (card == null){
            return new ResponseEntity<>("Card not found", HttpStatus.NOT_FOUND);
        }
        if (client.getCards().stream().noneMatch(card1 -> card1.getId() == id)){
            return new ResponseEntity<>("Card is not yours", HttpStatus.FORBIDDEN);
        }
        card.setActive(false);
        cardService.save(card);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @RequestMapping(path = "/api/clients/current/cards")
    public List<CardDTO> getCurrentCard(Authentication authentication) {
        return cardService.getCurrentCard(authentication);
    }









}
