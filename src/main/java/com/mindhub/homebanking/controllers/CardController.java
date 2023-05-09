package com.mindhub.homebanking.controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashSet;
import java.util.Random;

import java.time.LocalDate;
import java.util.Set;


@RestController
public class CardController {


    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(path = "/api/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createNewAccount(Authentication authentication,
                                                   @RequestParam String type,
                                                   @RequestParam String color ) {
        Client client = clientRepository.findByEmail(authentication.getName());
        int debitCant = 0;
        int creditCant= 0;
        Set<Card> debits = new HashSet<>();
        Set<Card> credits = new HashSet<>();

        for(Card card : client.getCards()){
            if(card.getType().equals(CardType.CREDIT)){
                creditCant++;
            } else if (card.getType().equals(CardType.DEBIT)){
                debitCant++;
            }
            if (card.getType().equals(CardType.valueOf(type)) && card.getColor().equals(CardColor.valueOf(color))) {
                return new ResponseEntity<>("already have 0" + type.toLowerCase() + "card whit this color", HttpStatus.FORBIDDEN);
            }
        }



        if(type.equals("CREDIT") && creditCant < 3){
            Card newCard = new Card (CardType.CREDIT, CardColor.valueOf(color),client.getFirstName() + client.getLastName(), LocalDate.now(), LocalDate.now().plusYears(5), randomNumber(), Integer.parseInt(randomCvv()));
            client.addCard(newCard);
            cardRepository.save(newCard);}
        else if (type.equals("DEBIT") && creditCant < 3) {
            Card newCard = new Card (CardType.DEBIT, CardColor.valueOf(color),client.getFirstName() + client.getLastName(), LocalDate.now(), LocalDate.now().plusYears(5), randomNumber(), Integer.parseInt(randomCvv()));
            client.addCard(newCard);
            cardRepository.save(newCard);
        }
        else {
        return new ResponseEntity<>("you can`t have more than 3 cards", HttpStatus.FORBIDDEN);
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










}
