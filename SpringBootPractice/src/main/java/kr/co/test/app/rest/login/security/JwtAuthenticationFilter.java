package kr.co.test.app.rest.login.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import common.util.json.JacksonUtil;
import common.util.map.ResultSetMap;
import kr.co.test.app.common.Constants;
import kr.co.test.app.common.ResponseCode;
import kr.co.test.app.page.login.model.AuthenticatedUser;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private JwtTokenProvider jwtTokenProvider; 
	
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		ResultSetMap resMap = new ResultSetMap();
		
		// 1. 헤더에서 토큰 가져오기
		String sToken = jwtTokenProvider.getTokenFromReqHeader(request);
		if ( StringUtils.isEmpty(sToken) ) {
			resMap.put(Constants.RESP.RESP_CD, ResponseCode.INVALID_TOKEN.getCode());
			resMap.put(Constants.RESP.RESP_MSG, ResponseCode.INVALID_TOKEN.getMessage());
			
		} else {
			AuthenticatedUser user = null;
			
			// 2. 토큰 유효성 검증
			switch ( jwtTokenProvider.isValidateJwtToken(sToken) ) {
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
				user = jwtTokenProvider.getAuthUserFromJwt(sToken);
				
				UsernamePasswordAuthenticationToken authentication 
				= new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);			
			}
		}
		
		if ( !resMap.isEmpty() ) {
			@SuppressWarnings("unchecked")
			String sMessage = JacksonUtil.ToJson.converterMapToJsonStr(resMap);
			
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(Constants.UTF8);
			response.getWriter().write(sMessage);
			
		} else {
			filterChain.doFilter(request, response);
		}
	}
	
}
