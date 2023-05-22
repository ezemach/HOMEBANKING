package com.mindhub.homebanking.service;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {


    void save (Loan loan);

    List<LoanDTO> getLoanDTO();

    Loan findById (Long id);


}
