package net.sasakonnect.wallet.controllers.sme;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.ConfirmOtp;
import net.sasakonnect.wallet.RequestDto.MpesaBilling;
import net.sasakonnect.wallet.RequestDto.WalletTransferDto;
import net.sasakonnect.wallet.RequestDto.sme.ChoiceSmeTransferDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeMpesaBilling;
import net.sasakonnect.wallet.RequestDto.sme.SmeTransferToMpesa;
import net.sasakonnect.wallet.RequestDto.sme.SmeWindowPinDto;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.TransactionMiddleware;
import net.sasakonnect.wallet.annotations.sme.HasSmeAccountPermission;
import net.sasakonnect.wallet.annotations.sme.HasSmePermission;
import net.sasakonnect.wallet.annotations.sme.SmeCorporate;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.constant.sme.GlobalSmeAccountPermissionConstants;
import net.sasakonnect.wallet.constant.sme.GlobalSmePermissionConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.SmeOtpDto;
import net.sasakonnect.wallet.services.sme.SmeTransactionService;
import net.sasakonnect.wallet.services.sme.SmeUserService;
import reactor.core.publisher.Mono;

@CustomController()

@RequestMapping("/sme/transaction")
@Tag(name = "SME Transaction", description = "SME transaction routes")
public class SmeTransactionController {
	@Autowired
	SmeTransactionService smeTransactionService;
	@Autowired
	SmeUserService smeUserservice;;	
	@SmeCorporate()
	@PostMapping("transferToWallet")
	@HasSmeAccountPermission(GlobalSmeAccountPermissionConstants.CanInvokeTransaction.PERMISSION)
	@TransactionMiddleware()
	public Object appplyWalletTransfer(@Valid @RequestBody() ChoiceSmeTransferDto choiceTransferdto) {
		return this.smeTransactionService.applyForTransfer(choiceTransferdto);
	}
	
	@SmeCorporate()
	@PostMapping("send/mpesa")
	@HasSmeAccountPermission(GlobalSmeAccountPermissionConstants.CanInvokeTransaction.PERMISSION)
	@TransactionMiddleware()
	public Object sendToMpesa(@Valid @RequestBody() SmeTransferToMpesa choiceTransferdto) {
		return this.smeTransactionService.withdrawToMpesa(choiceTransferdto);
	}
	
	@PostMapping("mpesa/payments")
	@TransactionMiddleware()
	public Object mpesaPayments(@RequestBody() @Valid SmeMpesaBilling tillAndBuyGoods) {
		return this.smeTransactionService.mpesaTillAndByGoods(tillAndBuyGoods);
	}
	
	@SmeCorporate
	@PostMapping("window/period")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetPermissions.PERMISSION)
	public ResponseEntity<ObjectNode> setWindowPeriod(@Valid @RequestBody() SmeWindowPinDto pinDto){
		return this.smeUserservice.setWindowPeriod(pinDto);
	}

	@SmeCorporate
	@PostMapping("window/confirmOtp")
	@HasSmePermission(GlobalSmePermissionConstants.CanGetPermissions.PERMISSION)
	public ResponseEntity<Object> verifywindowOtp(@Valid @RequestBody() ConfirmOtp pinDto){
		return this.smeUserservice.verifySmeWindowOtp(pinDto);
	}
	
}