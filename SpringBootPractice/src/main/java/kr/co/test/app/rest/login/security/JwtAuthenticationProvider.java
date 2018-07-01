package kr.co.test.app.rest.login.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import kr.co.test.app.page.login.model.AuthenticatedUser;
import kr.co.test.app.rest.login.model.JwtAuthenticationToken;

public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JwtTokenComponent jwtTokenComponent;

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		
		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
		String sToken = jwtAuthenticationToken.getToken();
		
		Claims clams = null;
		AuthenticatedUser user = null;
		
		try {
			clams = jwtTokenComponent.parseToken(sToken);
	        user = jwtTokenComponent.parseClaims(clams);
	        
		} catch (Exception e) {
			logger.error("", e);
		}
		
		/*
		if (user == null) {
			throw new JwtException("Token is not Valid");
		}
		*/
		
		return user;
	}

}
