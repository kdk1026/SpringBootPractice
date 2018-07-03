package kr.co.test.app.rest.login.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import common.LogDeclare;
import common.spring.resolver.ParamCollector;
import common.util.date.Jsr310DateUtil;
import common.util.map.ResultSetMap;
import kr.co.test.app.common.Constants;
import kr.co.test.app.common.ResponseCode;
import kr.co.test.app.page.login.model.AuthenticatedUser;
import kr.co.test.app.page.login.service.UserService;
import kr.co.test.app.rest.login.security.JwtTokenProvider;
import kr.co.test.app.rest.login.security.JwtTokenProvider.JwtToken;

@Service
public class RestLoginServiceImpl extends LogDeclare implements RestLoginService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Value("#{jwt}")
	private Properties jwtProp;
	
	@Override
	public ResultSetMap processAuthByUsername(ParamCollector paramCollector) {
		ResultSetMap resMap = new ResultSetMap();
		
		String username = paramCollector.getString(Constants.ID_PWD.USERNAME);
        String password = paramCollector.getString(Constants.ID_PWD.PASSWORD);
        // XXX : 비밀번호 구간 암호화 시, 복호화 처리
        
		AuthenticatedUser user = null;
		
		try {
			user = (AuthenticatedUser) userService.loadUserByUsername(username);
			
			if (user == null) {
				throw new RuntimeException("계정정보 없음");
			}
			
        	if ( !passwordEncoder.matches(password, user.getPassword()) ) {
        		throw new BadCredentialsException("비밀번호 불일치");
        	}
        	
        	if ( user.getLock() == 0 ) {
        		resMap.put(Constants.RESP.RESP_CD, ResponseCode.ACCESS_DENIED.getCode());
    			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.ACCESS_DENIED.getMessage());
    			return resMap;
        	}
        	
        	JwtToken jwtToken = jwtTokenProvider.generateToken(user);
        	
        	String sAccessToken = jwtToken.accessToken;
        	Date date = jwtTokenProvider.getExpirationFromJwt(sAccessToken);
        	
    		resMap.put("token_type", jwtProp.getProperty("jwt.tokenType"));
    		resMap.put("access_token", jwtToken.accessToken);
    		resMap.put("expires_in", (date.getTime() / 1000));
    		resMap.put("refresh_token", jwtToken.refreshToken);
    		
    		List<String> listAuthority = new ArrayList<String>();
    		List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();
    		for (GrantedAuthority auth : authorities) {
    			listAuthority.add(auth.getAuthority());
    		}
    		resMap.put("scope", listAuthority);
    		
    		resMap.put(Constants.RESP.RESP_CD, ResponseCode.S0000.getCode());
    		resMap.put(Constants.RESP.RESP_MSG, ResponseCode.S0000.getMessage());
			
		} catch (Exception e) {
			logger.error("", e);
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.LOGIN_INVALID.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.LOGIN_INVALID.getMessage());
		}
		
		return resMap;
	}

	@Override
	public AuthenticatedUser processAuthByToken(ParamCollector paramCollector) {
		String username = paramCollector.getString(Constants.ID_PWD.USERNAME);
		
		return (AuthenticatedUser) userService.loadUserByUsername(username);
	}

	@Override
	public ResultSetMap processRefresh(ParamCollector paramCollector) {
		ResultSetMap resMap = new ResultSetMap();
		
		String sGrantType = paramCollector.getString("grant_type");
		String sRefreshToken = paramCollector.getString("refresh_token");
		
		if ( !"refresh_token".equals(sGrantType) ) {
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.INVALID_TOKEN.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.INVALID_TOKEN.getMessage());
			return resMap;
		}
		
		try {
			String username = jwtTokenProvider.getUsernameFromJwt(sRefreshToken);
			paramCollector.put(Constants.ID_PWD.USERNAME, username);
			
			AuthenticatedUser user = this.processAuthByToken(paramCollector);
			
			JwtToken jwtToken = new JwtToken();
        	jwtToken.accessToken = jwtTokenProvider.generateAccessToken(user);
			
			Date date = jwtTokenProvider.getExpirationFromJwt(sRefreshToken);
			
			resMap.put("token_type", jwtProp.getProperty("jwt.tokenType"));
			resMap.put("access_token", jwtToken.accessToken);
			resMap.put("expires_in", (date.getTime() / 1000));
			
			jwtToken.refreshToken = jwtTokenProvider.generateRefreshToken(user);
			
    		String sExpireDate = Jsr310DateUtil.Convert.getDateToString(date);
    		int nGap = Jsr310DateUtil.GetDateInterval.intervalDays(sExpireDate);
    		
    		logger.debug("Refresh Token Expire Today Gap is {}", nGap);
    		
			if (nGap >= -7 && nGap <= 0) {
				jwtToken.refreshToken = jwtTokenProvider.generateRefreshToken(user);
				resMap.put("refresh_token", jwtToken.refreshToken);
			}
			
    		resMap.put(Constants.RESP.RESP_CD, ResponseCode.S0000.getCode());
    		resMap.put(Constants.RESP.RESP_MSG, ResponseCode.S0000.getMessage());
			
		} catch (Exception e) {
			logger.error("", e);
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.FFFFF.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.FFFFF.getMessage());
		}
		
		return resMap;
	}
	
}
