package net.sasakonnect.wallet.aspects;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.config.KonnectHeader;
import net.sasakonnect.wallet.services.WalletClientService;
import net.sasakonnect.wallet.tools.JwtService;

@Aspect
@Component
public class SdkMiddlewareAspect {
	private WalletClientService walletclientService;
	private JwtService jwtService;
	@Autowired
	private ClientAppsBean clientDataService;

	public SdkMiddlewareAspect(WalletClientService walletclientService, JwtService jwtService) {
		this.walletclientService = walletclientService;

		this.jwtService = jwtService;
	}

	@Before("@annotation(net.sasakonnect.wallet.annotations.SdkMiddleware)")

	public void beforeControllerMethodExecution() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		var client_app_key = request.getHeader(KonnectHeader.CLIENT_APP_KEY_HEADER.toString());
		if (client_app_key == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "merchant key  required");
			map.put("success", false);
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonError = objectMapper.writeValueAsString(map);
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, jsonError);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			var walletclient = this.walletclientService.findMerchantByClientAppByKey(client_app_key);
			if (walletclient.isPresent()) {
				var clients = walletclient.get();
				if (!clients.isEmpty()) {
					var client = clients.get(0);
					clientDataService.setWalletClient(client);
					return;
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "merchant key  required");
			map.put("success", false);
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonError = objectMapper.writeValueAsString(map);
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, jsonError);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
