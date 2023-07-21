package com.oscarmartinez.socialleague.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProvider {

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpiration}")
	private int jwtExpiration;

	public String generateJwtToken(Authentication authentication) {

		UserImp user = (UserImp) authentication.getPrincipal();
		return Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature -> Message: {} ", e);
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token -> Message: {}", e);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token -> Message: {}", e);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token -> Message: {}", e);
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty -> Message: {}", e);
		}

		return false;
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public Date getJwtTokenExpiration(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
	}
	
	public String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

}
