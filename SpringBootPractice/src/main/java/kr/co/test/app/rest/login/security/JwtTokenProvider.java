package kr.co.test.app.rest.login.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import kr.co.test.app.common.Constants;
import kr.co.test.app.page.login.model.AuthenticatedUser;

@Component
public class JwtTokenProvider {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("#{jwt}")
	private Properties jwtProp;

	public static class JwtToken {
		public String accessToken;
		public String refreshToken;
	}
	
	public Date getExpirationTime(String sExpireIn) {
		int nExpireIn = Integer.parseInt(sExpireIn);
    	return DateTime.now().plusHours(nExpireIn).toDate();
    }
	
	public JwtToken generateToken(AuthenticatedUser user) {
		JwtToken jwtToken = new JwtToken();
	 
		try {
			jwtToken.accessToken = this.generateAccessToken(user);
			jwtToken.refreshToken = this.generateRefreshToken(user);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	 
		return jwtToken; 
	}
	
	public String generateAccessToken(AuthenticatedUser user) throws Exception {
		JwtBuilder builder = Jwts.builder();
		builder.setId(user.getUsername())
	 		.setIssuedAt(new Date())
	 		.setSubject(Constants.Jwt.ACCESS_TOKEN)
	 		.setIssuer(jwtProp.getProperty("jwt.issuer"))
	 		.signWith(SignatureAlgorithm.HS256, jwtProp.getProperty("jwt.secret"))
	 		.setExpiration( this.getExpirationTime(jwtProp.getProperty("jwt.accessExpireHour")) );
		
	 	builder.claim(Constants.PERSONAL.NAME, user.getName());
	 	
		List<String> listAuthority = new ArrayList<String>();
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();
		for (GrantedAuthority auth : authorities) {
			listAuthority.add(auth.getAuthority());
		}
	 	builder.claim(Constants.Jwt.SCOPE, listAuthority);
		
	 	return builder.compact();
	}
	
	public String generateRefreshToken(AuthenticatedUser user) throws Exception {
		JwtBuilder builder = Jwts.builder();
		builder.setId(user.getUsername())
		.setIssuedAt(new Date())
		.setSubject(Constants.Jwt.REFRESH_TOKEN)
		.setIssuer(jwtProp.getProperty("jwt.issuer"))
		.signWith(SignatureAlgorithm.HS256, jwtProp.getProperty("jwt.secret"))
		.setExpiration( this.getExpirationTime(jwtProp.getProperty("jwt.refreshExpireHour")) );
		
	 	builder.claim(Constants.PERSONAL.NAME, user.getName());
	 	
		List<String> listAuthority = new ArrayList<String>();
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();
		for (GrantedAuthority auth : authorities) {
			listAuthority.add(auth.getAuthority());
		}
	 	builder.claim(Constants.Jwt.SCOPE, listAuthority);
		
		return builder.compact();
	}
	
	public Date getExpirationFromJwt(String token) throws Exception {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtProp.getProperty("jwt.secret"))
				.parseClaimsJws(token)
				.getBody();
		
		return claims.getExpiration();
	}
	
	public AuthenticatedUser getAuthUserFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProp.getProperty("jwt.secret"))
                .parseClaimsJws(token)
                .getBody();
        
		AuthenticatedUser user = new AuthenticatedUser();
        user.setUsername(claims.getId());
        user.setName(claims.get(Constants.PERSONAL.NAME).toString());
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        @SuppressWarnings("unchecked")
		List<String> listAuthority = (List<String>) claims.get(Constants.Jwt.SCOPE);
        for (String role : listAuthority) {
        	authorities.add(new SimpleGrantedAuthority(role));
        }
        user.setAuthorities(authorities);
        
        return user;   
	}
	
	public int isValidateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtProp.getProperty("jwt.secret")).parseClaimsJws(token);
			return 1;
			
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature");
			
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token");
			
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
			return 2;
			
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
			
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		
		return 0;
	}
	
}
