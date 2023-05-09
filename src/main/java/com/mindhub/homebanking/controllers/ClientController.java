package com.mindhub.homebanking.controllers;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;


    @RequestMapping("/api/clients")
    public List<ClientDTO> getClients() {

        return clientRepository
                .findAll()
                .stream()
                .map(ClientDTO::new)
                .collect(toList());
    }
    @RequestMapping("/api/clients/{id}")

    public ClientDTO getClients(@PathVariable long id){

    /*return repoClient.findById(id).map(client -> new ClientDTO(client)).orElse(null);*/
        Optional<Client> optionalClient = clientRepository.findById(id);
        return optionalClient.map(client -> new ClientDTO(client)).orElse(null);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @RequestMapping(path = "/api/clients", method = RequestMethod.POST)
    public ResponseEntity <Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        String accountNumber;
        do {
            int randomNumber = (int) (Math.random() * 100000000);
            accountNumber = "VIN" + String.format("%08d", randomNumber);
        } while (accountRepository.findByNumber(accountNumber) != null);
        Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        Account newAccount = new Account(accountNumber, LocalDateTime.now(),0);
        clientRepository.save(newClient);
        newClient.addAccount(newAccount);
        accountRepository.save(newAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @RequestMapping(path = "/api/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(clientRepository.findByEmail(authentication.getName()));
    }


}
