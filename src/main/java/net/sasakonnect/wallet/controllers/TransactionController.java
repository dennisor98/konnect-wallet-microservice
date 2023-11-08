package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;

@CustomController()
@RequestMapping("transaction")
@Tag(name = "transaction", description = "Transaction")

public class TransactionController {
	private final UserService userService;
	private final TransactionService transactionService;

	public TransactionController(UserService userService, TransactionService transactionService) {
		this.userService = userService;
		this.transactionService = transactionService;
	}

	@GetMapping("")
	public ResponseEntity<Optional<User>> getTransactions() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("/{id}")
	public Object getOneTransaction(@PathVariable String id) {
		if (id == null) {
			return ResponseEntity.notFound();
		}
		return this.transactionService.getTrasactionStatus(id);
	}

}
