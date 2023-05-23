package com.mindhub.homebanking.service;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface CardService {
//    void saveCard (Card card);

    Card findById(Long id);

    Card findByNumber (String number);

    void save(Card card);

    List<CardDTO> getCurrentCard(Authentication authentication);

}
