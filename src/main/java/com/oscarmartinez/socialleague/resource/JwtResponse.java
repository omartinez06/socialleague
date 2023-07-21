package com.oscarmartinez.socialleague.resource;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class JwtResponse {
	
	private String token;
	private String type = "Bearer";
	private String username;
	private Collection<? extends GrantedAuthority> authorities;
	private Date expirationDate;

	public JwtResponse(final String accessToken, final String username,
			final Collection<? extends GrantedAuthority> authorities, Date expirationDate) {
		this.token = accessToken;
		this.username = username;
		this.authorities = authorities;
		this.expirationDate = expirationDate;
	}

}
