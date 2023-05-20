package com.mindhub.homebanking.service;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getClients();

    ClientDTO getClientDTO(Long id);

    void saveClient (Client client);

    ClientDTO getCurrentClient(Authentication authentication);

    Client findByEmail(String email);

    void save(Client client);
}
