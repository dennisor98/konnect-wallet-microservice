package net.sasakonnect.wallet.controllers.sme;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.WalletTransferDto;
import net.sasakonnect.wallet.RequestDto.sme.ChoiceSmeTransferDto;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.sme.HasSmePermission;
import net.sasakonnect.wallet.annotations.sme.SmeCorporate;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.constant.sme.GlobalSmeAccountPermissionConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.sme.SmeTransactionService;
import reactor.core.publisher.Mono;

@CustomController()

@RequestMapping("/sme/transaction")
@Tag(name = "SME Transaction", description = "SME transaction routes")
public class SmeTransactionController {
	@Autowired
	SmeTransactionService smeTransactionService;
	
	@SmeCorporate()
	@GetMapping("transferToWallet")
	@HasSmePermission(GlobalSmeAccountPermissionConstants.CanInvokeTransaction.PERMISSION)
	public Object appplyWalletTransfer(@Valid @RequestBody() ChoiceSmeTransferDto choiceTransferdto) {
		return this.smeTransactionService.applyForTransfer(choiceTransferdto);
	}

}