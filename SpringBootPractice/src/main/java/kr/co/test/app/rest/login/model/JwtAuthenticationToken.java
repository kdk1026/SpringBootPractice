package kr.co.test.app.rest.login.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
	
	private static final long serialVersionUID = 1L;
	
	private String token;
	
	public JwtAuthenticationToken(String token) {
		super(null, null);
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return super.getCredentials();
	}

	@Override
	public Object getPrincipal() {
		return super.getPrincipal();
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		super.setAuthenticated(isAuthenticated);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
	}
	
}
