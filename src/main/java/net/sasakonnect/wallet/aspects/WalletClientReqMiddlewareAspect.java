package net.sasakonnect.wallet.aspects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.config.KonnectHeader;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.services.WalletClientService;


@Aspect
@Component
public class WalletClientReqMiddlewareAspect {
	@Autowired
	WalletClientService walletClientService;
	@Before("@annotation(net.sasakonnect.wallet.annotations.WalletClientReqMiddleware)")
	public void beforeControllerMethodExecution() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		var appKey = request.getHeader(KonnectHeader.CLIENT_APP_KEY_HEADER.toString());
		var appSecret =  request.getHeader(KonnectHeader.SECRET_APP_KEY_HEADER.toString());
		System.out.println("{{appSec}}"+appSecret);
		if (appKey == null || appSecret == null) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "appKey and appSecret is required");
			map.put("success", false);
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonError = objectMapper.writeValueAsString(map);
				throw new ResponseStatusException(HttpStatus.GONE, jsonError);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		Optional<List<WalletClient>> clientAccOpt =  this.walletClientService.findWalletClientByAppKey(appKey);
		if(clientAccOpt.isEmpty()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Unkown client");

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonError = objectMapper.writeValueAsString(map);
				throw new ResponseStatusException(HttpStatus.GONE, jsonError);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		var client =  clientAccOpt.get().get(0);
		if(!client.getEnabled()) {
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","client services disabled");

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonError = objectMapper.writeValueAsString(map);
				throw new ResponseStatusException(HttpStatus.GONE, jsonError);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
