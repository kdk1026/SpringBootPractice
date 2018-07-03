package kr.co.test.app.rest.login.security;

import java.util.Date;
import java.util.Properties;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.co.test.app.common.Constants;
import kr.co.test.app.page.login.model.AuthenticatedUser;

@Component
public class JwtTokenProvider {
	
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
		
		return builder.compact();
	}
	
	public String getUsernameFromJwt(String token) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProp.getProperty("jwt.secret"))
                .parseClaimsJws(token)
                .getBody();
        
		return claims.getId();
	}
	
	public Date getExpirationFromJwt(String token) throws Exception {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtProp.getProperty("jwt.secret"))
				.parseClaimsJws(token)
				.getBody();
		
		return claims.getExpiration();
	}
	
}
