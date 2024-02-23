package net.sasakonnect.wallet.aspects;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.config.KonnectHeader;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.JwtService;

@Aspect
@Component
public class TransactionMiddlewareAspect {
	private UserService userService;
	private JwtService jwtService;

	public TransactionMiddlewareAspect(UserService userService, JwtService jwtService) {
		this.userService = userService;

		this.jwtService = jwtService;
	}

	@Before("@annotation(net.sasakonnect.wallet.annotations.TransactionMiddleware)")
	public void beforeControllerMethodExecution() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		var transaction_token = request.getHeader(KonnectHeader.X_TRANSACTION_HEADER.toString());
		if (transaction_token == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "x-transaction-id is required");
			map.put("success", false);
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonError = objectMapper.writeValueAsString(map);
				throw new ResponseStatusException(HttpStatus.GONE, jsonError);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				var isValid = this.jwtService.validateToken(transaction_token, (User) authentication.getPrincipal());
				if (!isValid) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("message", "could not validate session");
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

			} catch (JwtException e) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message", "malformed token");
				map.put("success", false);

				ObjectMapper objectMapper = new ObjectMapper();
				try {
					String jsonError = objectMapper.writeValueAsString(map);
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, jsonError);

				} catch (JsonProcessingException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}
		}

	}
}
