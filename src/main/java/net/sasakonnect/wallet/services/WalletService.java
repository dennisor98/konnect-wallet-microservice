package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.enums.TransactionStatus;
import net.sasakonnect.wallet.enums.WalletTransactionType;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
public class WalletService extends JwtService {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	UserService userService;

	public Object getWalletInfo() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var reqId = new HashMap<String, Object>();
		reqId.put("userId", user.getId());
		var reqs = requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.GET_WALLET_INFO)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}

		return null;
	}

//
//	public ResponseEntity<> getBalance() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public Object getWalletBalance() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var user2 = this.userService.findUserWallet(user);

		if (user2.isPresent()) {
			var wallets = user2.get().getUserWallets();

			if (!wallets.isEmpty()) {
				var oneWallet = wallets.get(0);
				var reqId = new HashMap<String, Object>();
				reqId.put("accountId", oneWallet.getWallet().getAccountId());
				var reqs = requestSigner.signRequest(reqId);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.CHECK_BALANCE).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
						.bodyToMono(String.class);

				String responseJson = responseMono.block();

				if (responseJson != null) {
					return new Gson().fromJson(responseJson, Object.class);

				}
			}
		}

		return null;
	}

	public Object getTransactionHistory() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var user2 = this.userService.findUserWallet(user);
		List<WalletTransactionType> transactionTypes = new ArrayList<>(Arrays.asList(WalletTransactionType.TTID0001,
				WalletTransactionType.TTID0002, WalletTransactionType.TTID0003, WalletTransactionType.TTID0004,
				WalletTransactionType.TTID0005, WalletTransactionType.TTID0006, WalletTransactionType.TTID0007));

		// Create an ArrayList for TransactionStatus
		List<TransactionStatus> transactionStatuses = new ArrayList<>(
				Arrays.asList(TransactionStatus.TIMEOUT, TransactionStatus.PENDING, TransactionStatus.PROCESSING,
						TransactionStatus.FAILED, TransactionStatus.SUCCESS));

		if (user2.isPresent()) {
			var wallets = user2.get().getUserWallets();

			if (!wallets.isEmpty()) {
				var oneWallet = wallets.get(0);
				var reqId = new HashMap<String, Object>();
				reqId.put("userId", user.getId());
				reqId.put("accountId", oneWallet.getWallet().getAccountId());
				reqId.put("txType",
						transactionTypes.stream().map(WalletTransactionType::getValue).collect(Collectors.toList()));

				reqId.put("txStatus",
						transactionStatuses.stream().map(TransactionStatus::getValue).collect(Collectors.toList()));

				reqId.put("startTime", 1680419930226L);
				reqId.put("endTime", 1687245530226L);
				reqId.put("pageSize", 20);
				reqId.put("pageNo", 1);
				reqId.put("orderByDesc", 1);

				var reqs = requestSigner.signRequest(reqId);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.GET_TRANSACTIONS).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
						.bodyToMono(String.class);

				String responseJson = responseMono.block();

				if (responseJson != null) {
					return new Gson().fromJson(responseJson, Object.class);

				}
			}
		}

		return null;
	}

}
