package kr.co.test.app.page.login.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * @since 2018. 8. 19.
 * @author 김대광
 * @Description	: 
 * <pre>
 * maxSessionsPreventsLogin(false) 인 경우에만 유효
 * expiredUrl은 Security 4.2 이하에서만 동작 
 * -----------------------------------
 * 개정이력
 * 2018. 8. 19. 김대광	최초작성
 * </pre>
 */
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent eventØ) throws IOException, ServletException {
		HttpServletRequest request = eventØ.getRequest();
		String sCtx = request.getContextPath();
		
		StringBuilder sb = new StringBuilder();
		sb.append("<script type='text/javascript'>");
		sb.append("alert('다른 곳에서 로그인 되어 자동 로그아웃 됩니다.'); top.location.href = "+ sCtx +"'/admin_logout';");
		sb.append("</script>");
		
		HttpServletResponse response = eventØ.getResponse();
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.getWriter().print( sb.toString() );
		response.flushBuffer();
	}

}
