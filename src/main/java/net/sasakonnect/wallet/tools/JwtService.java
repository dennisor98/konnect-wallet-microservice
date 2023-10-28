package net.sasakonnect.wallet.tools;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import net.sasakonnect.wallet.domain.User;

@Service
public class JwtService {
	byte[] decodedKey = Base64.getDecoder().decode("579e6ba03d08e5f57c4ec2dd9b4a8ea9ac3168f5518d1909206e46f405f32a49");
	SecretKeySpec secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HMACSHA256");

	public String generateToken(User user) {
		try {
			Map<String, Object> claims = new HashMap<>();
			claims.put("id", user.getId());
			claims.put("firstName", user.getFirstName());
			return Jwts.builder().setClaims(claims).setSubject(user.getId().toString()).setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 864000000))// 10 days validity
					.setId(UUID.randomUUID().toString())

					.signWith(secretKey, SignatureAlgorithm.HS256).compact();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String generateTokenForWindow(User user) {
		try {
			Map<String, Object> claims = new HashMap<>();
			claims.put("id", user.getId());
			claims.put("firstName", user.getFirstName());
			return Jwts.builder().setClaims(claims).setSubject(user.getId().toString()).setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 60000))// 10 days validity
					.setId(UUID.randomUUID().toString())

					.signWith(secretKey, SignatureAlgorithm.HS256).compact();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String generateRefreshToken(User user) {
		try {
			Map<String, Object> claims = new HashMap<>();
			claims.put("id", user.getId());
			claims.put("firstName", user.getFirstName());
			return Jwts.builder().setClaims(claims).setSubject(user.getId().toString()).setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 10000))// 10 days validity
					.setId(UUID.randomUUID().toString())

					.signWith(secretKey, SignatureAlgorithm.HS256).compact();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		try {
			final String username = extractUsername(token);
			System.out.println(username);
			System.out.println(userDetails.getUsername());
			User user = (User) userDetails;
			return (username.equals(user.getId()) && !isTokenExpired(token));
		} catch (MalformedJwtException e) {
			e.printStackTrace();
		}
		return false;

	}

	public String extractUsername(String token) throws MalformedJwtException {
		try {
			if (Jwts.parserBuilder().setSigningKey(secretKey).build().isSigned(token)) {
				Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
				return claims.getSubject();

			} else {
				return null;
			}

		} catch (MalformedJwtException e) {
			throw e;
		}

	}

	private Date extractExpiration(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
		return claims.getExpiration();
	}

	private boolean isTokenExpired(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
		System.out.println(claims.getExpiration());
		System.out.println(new Date());
		System.out.println(claims.getExpiration().after(new Date()));
		return claims.getExpiration().before(new Date());
	}

}
