package net.sasakonnect.wallet.aspects;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.enums.JwtType;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.JwtService;

@Aspect
@Component
public class UserTokenValidationAspect {
	private JwtService jwtService;
	private UserService userService;
	
	@Value("${JWT_SECRET}")
	String jwtSecret;
	
	public UserTokenValidationAspect(UserService userService, JwtService jwtService) {
		this.userService = userService;

		this.jwtService = jwtService;
	}
	
	byte[] decodedKey =  null;
	SecretKeySpec secretKey = null;
	
	@PostConstruct()
	void init(){
		decodedKey = Base64.getDecoder().decode(jwtSecret);
		secretKey =	new SecretKeySpec(decodedKey, 0, decodedKey.length, "HMACSHA256");
	}

	@Before("execution(* *(..)) && @within(net.sasakonnect.wallet.annotations.IsUser)")
	public void beforeControllerMethodExecution() {
		 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
	        }

	        String token = authorizationHeader.substring(7);
	        try {
	        	Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	            String tokenType = claims.get("token_type", String.class);

	            if (!JwtType.ACCESS_TOKEN.getToken().equals(tokenType)) {
	                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token type");
	            }
	            // Set the authenticated user in the security context
	            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();;
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        } catch (JwtException ex) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
	        }
	}
	
}
