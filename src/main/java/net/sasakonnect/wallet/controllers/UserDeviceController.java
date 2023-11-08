package net.sasakonnect.wallet.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;

@RequestMapping("user-device")
@CustomController()
@Tag(name = "user-device", description = "User  Device")

public class UserDeviceController {
	private final UserService userService;

	public UserDeviceController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("")
	public ResponseEntity<Optional<User>> addDevice() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("")
	public ResponseEntity<Optional<User>> getDevices() {
		return ResponseEntity.ok(userService.getUserById("0"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<User>> getOneDevice(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Optional<User>> updateDevice(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Optional<User>> deleteDevice(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}
}
