package net.sasakonnect.wallet.controllers.sme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.sme.SmeUserLogin;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.services.sme.SmeUserService;

@RequestMapping("/sme")
@Tag(name = "SME", description = "SME User  routes")
@CustomController()
@Slf4j
@RestController()
public class SmeController {
	@Autowired
	SmeUserService smeUserService;
	
	@PostMapping("/login")
	public ResponseEntity<ObjectNode> smeLogin(@Valid @RequestBody SmeUserLogin loginDto ){
		return this.smeUserService.smeLogin(loginDto);
	}
	
	
	

}
