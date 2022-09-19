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
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository){
		return (args) -> {
			Client client1 = new Client("Melba", "Morel", "melbamorel@gmail.com" , passwordEncoder.encode("melba"));
			Client client2 = new Client("Alan", "Mermolia", "mermolia.alan@gmail.com", passwordEncoder.encode("elalandos"));
			Client client3 = new Client("Pedro", "Picapiedra", "pedritomalvado@gmail.com", passwordEncoder.encode("elpica"));
			Client client4 = new Client("Admin", "Admin", "admin@gmail.com", passwordEncoder.encode("admin"));
			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.00, client1, AccountType.CURRENT);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500.00, client1, AccountType.SAVING);
			Account account3 = new Account("VIN003", LocalDateTime.now(), 73500.00, client2, AccountType.CURRENT);
			Account account4 = new Account("VIN004", LocalDateTime.now(), 22500.00, client3, AccountType.SAVING);

			clientRepository.save(client1);
			clientRepository.save(client2);
			clientRepository.save(client3);
			clientRepository.save(client4);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);

			Transaction transaction1 = new Transaction("Epic Games", 1500.00, LocalDateTime.now(), account1, TransactionType.DEBIT);
			Transaction transaction2 = new Transaction("Mc Donalds", 1204.99, LocalDateTime.now().minusDays(3), account1, TransactionType.DEBIT);
			Transaction transaction3 = new Transaction("Expensas", 15000.00, LocalDateTime.now().minusDays(7), account2, TransactionType.CREDIT);
			Transaction transaction4 = new Transaction("otros", 50704.25, LocalDateTime.now().minusDays(15), account1, TransactionType.CREDIT);
			Transaction transaction5 = new Transaction("Alquiler", 30000.00, LocalDateTime.now().minusDays(10), account2, TransactionType.DEBIT);
			Transaction transaction6 = new Transaction("Riot Games", 5500.00, LocalDateTime.now(), account1, TransactionType.DEBIT);
			Transaction transaction7 = new Transaction("Mc Donalds", 1204.99, LocalDateTime.now().minusDays(3), account1, TransactionType.DEBIT);
			Transaction transaction8 = new Transaction("Expensas", 15000.00, LocalDateTime.now().minusDays(9), account1, TransactionType.CREDIT);
			Transaction transaction9 = new Transaction("otros", 30209.25, LocalDateTime.now().minusDays(23), account2, TransactionType.CREDIT);
			Transaction transaction10 = new Transaction("servicios", 15000.00, LocalDateTime.now().minusDays(27), account2, TransactionType.DEBIT);
			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);
			transactionRepository.save(transaction9);
			transactionRepository.save(transaction10);

			Loan loan1 = new Loan("Hipotecario", 500000.00, List.of(12,24,36,48,60), 0.20);
			Loan loan2 = new Loan("Personal", 100000.00, List.of(6,12,24), 0.10);
			Loan loan3 = new Loan("Automotriz", 300000.00, List.of(6,12,24,36), 0.15);
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400000.00, 60, client1, loan1);
			ClientLoan clientLoan2 = new ClientLoan(50000.00, 12, client1, loan2);
			ClientLoan clientLoan3 = new ClientLoan(100000.00, 24, client2, loan2);
			ClientLoan clientLoan4 = new ClientLoan(200000.00, 36, client2, loan3);
			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

			Card card1 = new Card(client1, CardType.DEBIT, CardColor.GOLD, "2542 3565 2354 5458", 167, LocalDateTime.now(), LocalDateTime.now().plusYears(5));
			Card card2 = new Card(client1, CardType.CREDIT, CardColor.TITANIUM, "4512 7812 2456 0755", 648, LocalDateTime.now(), LocalDateTime.now().plusYears(5));
			Card card3 = new Card(client2, CardType.CREDIT, CardColor.SILVER, "4527 6785 8767 4562", 223, LocalDateTime.now(), LocalDateTime.now().plusYears(5));
			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);
		};

	}
}
