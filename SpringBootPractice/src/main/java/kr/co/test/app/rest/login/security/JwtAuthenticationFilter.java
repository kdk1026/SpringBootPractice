package kr.co.test.app.rest.login.security;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import common.spring.resolver.ParamCollector;
import common.util.json.JacksonUtil;
import common.util.map.ResultSetMap;
import io.jsonwebtoken.ExpiredJwtException;
import kr.co.test.app.common.Constants;
import kr.co.test.app.common.ResponseCode;
import kr.co.test.app.page.login.model.AuthenticatedUser;
import kr.co.test.app.rest.login.service.RestLoginService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private JwtTokenProvider jwtTokenProvider; 
	private RestLoginService restLoginService;
	private Properties jwtProp;
	
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RestLoginService restLoginService, Properties jwtProp) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.restLoginService = restLoginService;
		this.jwtProp = jwtProp;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		ResultSetMap resMap = new ResultSetMap();
		
		// 1. 헤더에서 토큰 가져오기
		String sToken = null;
		String sJwtHeader = request.getHeader(jwtProp.getProperty("jwt.header"));
		if ( sJwtHeader.startsWith(jwtProp.getProperty("jwt.tokenType")) ) {
			sToken = sJwtHeader.substring(jwtProp.getProperty("jwt.tokenType").length());
		}
		
		if ( StringUtils.isEmpty(sToken) ) {
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.INVALID_TOKEN.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.INVALID_TOKEN.getMessage());
			
		} else {
			// 2. 토큰에서 아이디 추출
			String sUsername = "";
			
			try {
				sUsername = jwtTokenProvider.getUsernameFromJwt(sToken);
				
			} catch (ExpiredJwtException e) {
				resMap.put(Constants.RESP.RESP_CD, ResponseCode.TOKEN_EXPIRED.getCode());
				resMap.put(Constants.RESP.RESP_MSG, ResponseCode.TOKEN_EXPIRED.getMessage());
				
			} catch (Exception e) {
				logger.error("", e);
				resMap.put(Constants.RESP.RESP_CD, ResponseCode.FFFFF.getCode());
				resMap.put(Constants.RESP.RESP_MSG, ResponseCode.FFFFF.getMessage());
			}
			
			// 3. 로그인 정보 조회
			ParamCollector paramCollector = new ParamCollector();
			paramCollector.put(Constants.ID_PWD.USERNAME, sUsername);
			AuthenticatedUser user = restLoginService.processAuthByToken(paramCollector);
			
			UsernamePasswordAuthenticationToken authentication 
				= new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);			
		}
		
		if ( !resMap.isEmpty() ) {
			@SuppressWarnings("unchecked")
			String sMessage = JacksonUtil.converterMapToJsonStr(resMap);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.UTF8);
			response.getWriter().write(sMessage);
			
		} else {
			filterChain.doFilter(request, response);
		}
	}
	
}
