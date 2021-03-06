package kr.co.test.app.common.spring.component.aspect;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import common.LogDeclare;
import common.spring.resolver.ParamCollector;
import common.util.RequestUtil;
import common.util.json.GsonUtil;
import common.util.map.ParamMap;
import common.util.map.ResultSetMap;

@Component
@Aspect
public class ReqResAspect extends LogDeclare {

	@Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controllerBean() {}

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void restControllerBean() {}
	
	@Around("controllerBean() || restControllerBean()")
	public Object before(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		
		this.requestLog(args);
		this.responseLog(pjp);
		
		return pjp.proceed();
	}
	
	@After("controllerBean() || restControllerBean()")
	public void after(JoinPoint jp) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse response = attr.getResponse();
		
		response.setHeader("test", "test1234");
	}
	
	private void requestLog(Object[] args) {
		try {
			ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = sra.getRequest();

			String sKey = "";
			String sVal = "";
			
			String sRemoteAddr = StringUtils.defaultString(RequestUtil.getRequestIpAddress(request));
			String sAgent = StringUtils.defaultString(request.getHeader("User-Agent"));
			String sCookie = "";
			String sRequestHeader = "";
			
			Map<String, Object> cookieMap = new HashMap<>();
			Cookie[] cookies = request.getCookies();
			
			if ( cookies != null ) {
				for (Cookie c : cookies) {
					sKey = c.getName();
					sVal = c.getValue();
					cookieMap.put(sKey, sVal);
				}
				
				sCookie = GsonUtil.ToJson.converterMapToJsonStr(cookieMap);
			}
			
			Enumeration<?> headerNames = request.getHeaderNames();
			Map<String, Object> headerMap = new HashMap<>();
			while (headerNames.hasMoreElements()) {
				sKey = (String) headerNames.nextElement();
				sVal = request.getHeader(sKey);
				headerMap.put(sKey, sVal);
			}
			String[] sRemoveKeys = {
					"accept-language", "host", "upgrade-insecure-requests", "connection", "cache-control",
					"accept-encoding", "user-agent", "accept", "content-length", "cookie"
			};
			for (String s : sRemoveKeys) {
				headerMap.remove(s);
			}
			sRequestHeader = GsonUtil.ToJson.converterMapToJsonStr(headerMap);
			
			String sUri = StringUtils.defaultString(request.getRequestURI());
			
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
			String sPathVariable = GsonUtil.ToJson.converterMapToJsonStr(map);
			
	        ParamCollector paramCollector = new ParamCollector();
	        for (Object obj : args) {
	        	if (obj instanceof ParamCollector) {
	        		paramCollector = (ParamCollector)obj;
	        	}
	        }
	        
	        ParamMap paramMap = paramCollector.getMap();
	        ParamMap newParamMap = new ParamMap();
	        
	        if ( !paramMap.isEmpty() ) {
	        	newParamMap.putAll(paramMap);
	        	
	        	// 파일의 경우, 파일명만 로깅 처리
	        	sKey = "";
	        	Object obj = null;
	        	Iterator<String> it = paramMap.keySet().iterator();
	        	while ( it.hasNext() ) {
	        		sKey = it.next();
	        		obj = newParamMap.get(sKey);
	        		
	        		if ( obj instanceof MultipartFile ) {
	        			MultipartFile mFile = (MultipartFile) obj;
	        			newParamMap.put(sKey, mFile.getOriginalFilename());
	        		}
	        	}
	        }
	        
	        String sParameter = GsonUtil.ToJson.converterMapToJsonStr(newParamMap);
			
			logger.info("[{}] Agent - {}", sRemoteAddr, sAgent);
			logger.info("[{}] Cookie - {}", sRemoteAddr, sCookie);
			logger.info("[{}] Request Header - {}", sRemoteAddr, sRequestHeader);
			logger.info("[{}] URI - {}", sRemoteAddr, sUri);
			logger.info("[{}] PathVariable - {}", sRemoteAddr, sPathVariable);
			logger.info("[{}] Parameter - {}", sRemoteAddr, sParameter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void responseLog(ProceedingJoinPoint jp) {
		try {
			ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = sra.getRequest();
			
			String sRemoteAddr = StringUtils.defaultString(RequestUtil.getRequestIpAddress(request));
			
			Object result = jp.proceed();
			
			if ( result instanceof ResultSetMap ) {
				ResultSetMap resMap = (ResultSetMap) result;
				if ( !resMap.isEmpty() ) {
					@SuppressWarnings("unchecked")
					String sResponse = GsonUtil.ToJson.converterMapToJsonStr(resMap);
					
					logger.info("[{}] Response - {}", sRemoteAddr, sResponse);
				}
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
