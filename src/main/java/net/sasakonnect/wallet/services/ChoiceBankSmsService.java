package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ChoiceBankSmsService {
	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	ExecutorService executor = Executors.newFixedThreadPool(5);

	public Object invokeSms(String businessId) {
		executor.submit(() -> {

			var reqId = new HashMap<String, Object>();
			reqId.put("businessId", businessId);
			reqId.put("otpType", "sms");
			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.COMMON_SEND_OTP).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();
			log.info(responseJson);
			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
			return null;

		});

		return null;

	}

	public ResponseEntity invokeResendSms(String businessId) {
		executor.submit(() -> {

			var reqId = new HashMap<String, Object>();
			reqId.put("businessId", businessId);
			reqId.put("otpType", "sms");
			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.REQUEST_OTP_RESEND).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();
			log.info(responseJson);
			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
			return null;

		});

		return null;

	}

	public Object invokeResendSms(String businessId, String smsType) {
		executor.submit(() -> {

			var reqId = new HashMap<String, Object>();
			reqId.put("businessId", businessId);
			reqId.put("otpType", smsType);
			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.REQUEST_OTP_RESEND).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();
			log.info(responseJson);
			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
			return null;

		});

		return null;

	}

	public Object invokeSms(String businessId, String smsType) {
		executor.submit(() -> {

			var reqId = new HashMap<String, Object>();
			reqId.put("businessId", businessId);
			reqId.put("otpType", smsType);
			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.COMMON_SEND_OTP).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();
			log.info(responseJson);
			if (responseJson != null) {
				return new Gson().fromJson(responseJson, Object.class);

			}
			return null;

		});

		return null;

	}

	public Object confirmOperation(String businessId, String otpCode) {

		var reqId = new HashMap<String, Object>();
		reqId.put("businessId", businessId);
		reqId.put("otpCode", otpCode);
		var reqs = requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.CONFIRM_OTP)
				.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

		String responseJson = responseMono.block();
		log.info(responseJson);

		if (responseJson != null) {
			return new Gson().fromJson(responseJson, Object.class);

		}
		return null;

	}
}
