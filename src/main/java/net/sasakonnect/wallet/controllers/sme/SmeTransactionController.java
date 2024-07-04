package net.sasakonnect.wallet.controllers.sme;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.WalletTransferDto;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.sme.SmeCorporate;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import reactor.core.publisher.Mono;

@CustomController()

@RequestMapping("/sme/transaction")
@Tag(name = "SME Transaction", description = "SME transaction routes")
public class SmeTransactionController {
	@SmeCorporate()
	@GetMapping("transferToWallet")
	public Object appplyWalletTransfer() {
		return null;
	}

}
