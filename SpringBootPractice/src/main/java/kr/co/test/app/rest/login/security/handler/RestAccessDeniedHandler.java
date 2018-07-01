package kr.co.test.app.rest.login.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import common.util.ResponseUtil;
import common.util.json.JacksonUtil;
import common.util.map.ResultSetMap;
import kr.co.test.app.common.Constants;
import kr.co.test.app.common.ResponseCode;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		ResultSetMap resMap = new ResultSetMap();
		resMap.put(Constants.RESP.RESP_CD, ResponseCode.ACCESS_DENIED.getCode());
		resMap.put(Constants.RESP.RESP_MSG, ResponseCode.ACCESS_DENIED.getMessage());
		
		String sMessage = JacksonUtil.converterMapToJsonStr(resMap);
		
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		ResponseUtil.setJsonResponse(response, sMessage);
	}
	
}
