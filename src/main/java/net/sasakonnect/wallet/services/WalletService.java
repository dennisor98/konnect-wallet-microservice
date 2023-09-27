package net.sasakonnect.wallet.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.EndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.Signature;
import net.sasakonnect.wallet.tools.security.SignRequest;
import reactor.core.publisher.Mono;

@Service
public class WalletService extends JwtService {
	@Autowired
	BankWebClientBean bankClientBean;

	public Mono<String> getWalletInfo() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var sign = new Signature();
		var signRequest = new SignRequest();
		signRequest.setLocale("en_KE");
		signRequest.setParams(Map.of("userId", user.getId()));
		signRequest.setSender("");

		sign.build(signRequest);

		var results = sign.build(signRequest);

		// Verify the signature (assuming you have an appropriate method for it)
		boolean isValid = sign.verify(results);
		System.out.println(isValid);

		var res = this.bankClientBean.webClient.post().uri(EndpointsConstants.GET_WALLET_INFO).bodyValue(results)
				.retrieve().bodyToMono(String.class);

		res.subscribe(result -> {
			// Print the result when it's available
			System.out.println("Response: " + result);
		}, error -> {
			// Handle any errors that occur during the request
			System.err.println("Error: " + error.getMessage());
		});
		return res;
	}
//
//	public ResponseEntity<> getBalance() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
