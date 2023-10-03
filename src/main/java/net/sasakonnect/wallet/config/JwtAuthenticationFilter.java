
package net.sasakonnect.wallet.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	UserService userService;
	@Autowired
	JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("hello");
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String id = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			id = jwtService.extractUsername(token);
		}

		if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<User> user = this.userService.findUserWallet(id);
			UserDetails userDetails = userService.loadUserByUsername(id);
			if (user.isPresent() && this.jwtService.validateToken(token, userDetails)) {
				System.out.println("this is do internal");

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, null);
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);

	}

}
