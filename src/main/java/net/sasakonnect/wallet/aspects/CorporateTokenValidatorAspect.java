package net.sasakonnect.wallet.aspects;

import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.config.KonnectHeader;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.JwtService;

@Aspect
@Component
public class CorporateTokenValidatorAspect {
	private JwtService jwtService;
	private UserService userService;
	
	@Value("${JWT_SECRET}")
	String jwtSecret;
	
	public CorporateTokenValidatorAspect(UserService userService, JwtService jwtService) {
		this.userService = userService;

		this.jwtService = jwtService;
	}

	@Before("@annotation(net.sasakonnect.wallet.annotations.IsCorporate)")
	public void beforeControllerMethodExecution() {
		 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
	        }

	        String token = authorizationHeader.substring(7);
	        try {
	            Claims claims = Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token).getBody();
	            String tokenType = claims.get("token_type", String.class);

	            if (!"corporate_access_token".equals(tokenType)) {
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
