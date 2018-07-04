package kr.co.test.app.rest.login.service;

import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
		
		// 1. 파라미터 유효성 체크
		if ( !"refresh_token".equals(sGrantType) || StringUtils.isEmpty(sRefreshToken) ) {
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.INVALID_TOKEN.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.INVALID_TOKEN.getMessage());
			return resMap;
		}
		
		try {
			// 2. 토큰 유효성 검증
			switch ( jwtTokenProvider.isValidateToken(sRefreshToken) ) {
			case 0:
				resMap.put(Constants.RESP.RESP_CD, ResponseCode.INVALID_TOKEN.getCode());
				resMap.put(Constants.RESP.RESP_MSG, ResponseCode.INVALID_TOKEN.getMessage());				
				break;
			case 2:
				resMap.put(Constants.RESP.RESP_CD, ResponseCode.TOKEN_EXPIRED.getCode());
				resMap.put(Constants.RESP.RESP_MSG, ResponseCode.TOKEN_EXPIRED.getMessage());				
				break;

			default:
				break;
			}
			
			if ( resMap.isEmpty() ) {
				// 3. 토큰에서 로그인 정보 추출
				AuthenticatedUser user = jwtTokenProvider.getAuthUserFromJwt(sRefreshToken);
				
				// 4. 토큰 갱신
				// 4.1. Access 토큰 갱신
				JwtToken jwtToken = new JwtToken();
	        	jwtToken.accessToken = jwtTokenProvider.generateAccessToken(user);
	        	
	        	Date date = jwtTokenProvider.getExpirationFromJwt(sRefreshToken);
	        	
				resMap.put("token_type", jwtProp.getProperty("jwt.tokenType"));
				resMap.put("access_token", jwtToken.accessToken);
				resMap.put("expires_in", (date.getTime() / 1000));
				
				// 4.2. Refresh 토큰 갱신 조건 검증
	    		String sExpireDate = Jsr310DateUtil.Convert.getDateToString(date);
	    		int nGap = Jsr310DateUtil.GetDateInterval.intervalDays(sExpireDate);
	    		
				if (nGap >= -7 && nGap <= 0) {
					// 4.2.1. 충족 시, Refresh 토큰 갱신 응답
					jwtToken.refreshToken = jwtTokenProvider.generateRefreshToken(user);
					resMap.put("refresh_token", jwtToken.refreshToken);
				} else {
					// 4.2.2. 미충족 시, 파라미터 응답
					resMap.put("refresh_token", sRefreshToken);
				}
				
	    		resMap.put(Constants.RESP.RESP_CD, ResponseCode.S0000.getCode());
	    		resMap.put(Constants.RESP.RESP_MSG, ResponseCode.S0000.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("", e);
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.FFFFF.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.FFFFF.getMessage());
		}
		
		return resMap;
	}
	
}
