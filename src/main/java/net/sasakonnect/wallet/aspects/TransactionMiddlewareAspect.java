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

enum KonnectHeader {
	X_TRANSACTION_HEADER("x-transaction-id");

	private String value;

	KonnectHeader(String string) {
		this.value = string;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return this.value;
	}
}

@Aspect
@Component
public class TransactionMiddlewareAspect {
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

		}

	}
}
