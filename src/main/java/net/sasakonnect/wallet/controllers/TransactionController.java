package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sasakonnect.wallet.CustomController;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;

@CustomController()
@RequestMapping("transaction")
@Tag(name = "transaction", description = "Transaction")

public class TransactionController {
	private final UserService userService;

	public TransactionController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("")
	public ResponseEntity<Optional<User>> getTransactions() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<User>> getOneTransaction(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

}
