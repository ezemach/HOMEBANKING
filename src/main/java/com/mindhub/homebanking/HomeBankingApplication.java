package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class HomeBankingApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomeBankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {

			Client client1 = new Client("Melba", "Morel", "melba@hotmail.com", passwordEncoder.encode("m"));
			Client client2 = new Client("Chloe", "O'Brian" , "pepe@hotmail.com", passwordEncoder.encode("d") );
			Client admin = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("admin"));


			Account account1 = new Account("VIN001",LocalDateTime.now(),600000.0, true, AccountType.SAVING);
			Account account2 = new Account("VIN002",LocalDateTime.now(),75000.0, true, AccountType.CURRENT);
			Account account3 = new Account("VIN003",LocalDateTime.now(),42000.0, true, AccountType.SAVING);
			Account account4 = new Account("VIN004",LocalDateTime.now(),350000.0, true, AccountType.CURRENT);
			client1.addAccount(account1);
			client1.addAccount(account2);
			client2.addAccount(account3);
			client2.addAccount(account4);

			Transaction transaction1 = new Transaction(TransactionType.DEBIT, 220.20, "Debito Tarjeta credito", LocalDateTime.now(),6250.40);
			Transaction transaction2 = new Transaction(TransactionType.CREDIT, 800.50, "Credito Prestamo", LocalDateTime.now(), 5449.90);;
			Transaction transaction3 = new Transaction(TransactionType.DEBIT, 550.10, "Debito personal", LocalDateTime.now(), 5000);
			Transaction transaction4 = new Transaction(TransactionType.CREDIT, 90.00, "credito estatal", LocalDateTime.now(), 7489.8);;
			Transaction transaction5 = new Transaction(TransactionType.DEBIT, 10.20, "Debito Servicio", LocalDateTime.now(), 7500.0);;
			account1.addTransaction(transaction1);
			account1.addTransaction(transaction2);
			account1.addTransaction(transaction3);
			account2.addTransaction(transaction4);
			account2.addTransaction(transaction5);

			Loan loanHipotecario = new Loan ("Hipotecario",500000, Arrays.asList(12,24,36,48,60),5.0 );
			Loan loanPersonal = new Loan ("Personal",100000, Arrays.asList(6,12,24), 8.0);
			Loan loanAutomotriz = new Loan ("Automotriz",300000, Arrays.asList(6,12,24,36), 6.5);

			ClientLoan loan1 = new ClientLoan(450000,60,"Hipotecario" );
			ClientLoan loan2 = new ClientLoan(550000,12,"Personal" );
			ClientLoan loan3 = new ClientLoan(180000,24,"Personal");
			ClientLoan loan4 = new ClientLoan(200000,36, "Automotriz");

			client1.addClientLoan(loan1);
			client1.addClientLoan(loan2);
			client1.addClientLoan(loan3);
			client1.addClientLoan(loan4);

			loanHipotecario.addClientLoan(loan1);
			loanPersonal.addClientLoan(loan2);
			loanPersonal.addClientLoan(loan3);
			loanAutomotriz.addClientLoan(loan4);

			Card card1 =  new Card(  CardType.DEBIT, CardColor.GOLD,"Melba Morel",  LocalDate.now().minusMonths(2), LocalDate.now(), "5555 4445 4688 8897", 354, true);
			Card card2 =  new Card(  CardType.CREDIT, CardColor.TITANIUM,"Melba Morel", LocalDate.now(), LocalDate.now().plusYears(5),"4845 8822 7766 3345", 466, true);
			Card card3 =  new Card( CardType.DEBIT, CardColor.GOLD, "Chloe O'Brian", LocalDate.now(), LocalDate.now().plusYears(5), "4845 8822 7789 0090", 189, true);

			client1.addCard(card1);
			client1.addCard(card2);
			client2.addCard(card3);

			clientRepository.save(client1);
			clientRepository.save(client2);
			clientRepository.save(admin);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);

			loanRepository.save(loanHipotecario);
			loanRepository.save(loanPersonal );
			loanRepository.save(loanAutomotriz);

			clientLoanRepository.save(loan1);
			clientLoanRepository.save(loan2);
			clientLoanRepository.save(loan3);
			clientLoanRepository.save(loan4);

			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);


		};
	}
}


