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

			throw new ResponseStatusException(HttpStatus.GONE, map.toString());
		} else {
			var isValid = this.jwtService.validateToken(transaction_token, (User) authentication.getPrincipal());
			if (!isValid) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message", "could not validate session");
				map.put("success", false);
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, map.toString());

			}
		}

	}
}
