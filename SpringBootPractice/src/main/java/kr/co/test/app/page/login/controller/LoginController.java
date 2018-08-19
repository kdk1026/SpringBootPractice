package kr.co.test.app.page.login.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import common.LogDeclare;
import common.annotation.RsaKeyGenerator;
import common.spring.resolver.ParamCollector;
import common.util.map.ResultSetMap;
import common.util.sessioncookie.CookieUtilVer2;
import kr.co.test.app.common.Constants;
import kr.co.test.app.page.login.model.AuthenticatedUser;
import kr.co.test.app.page.login.service.LoginService;

@Controller
@RequestMapping("/admin/login")
public class LoginController extends LogDeclare {
	
	@Autowired
	private LoginService loginService;

	@RsaKeyGenerator
	@GetMapping
	public String loginForm(ParamCollector paramCollector, Model model, Authentication authentication) {
		AuthenticatedUser user = null;
		if (authentication != null) {
			user = (AuthenticatedUser) authentication.getPrincipal();
		}
		
		ResultSetMap resMap = new ResultSetMap();
		String sUri = "";
		
		if ( paramCollector.containsKey("error") ) {
			resMap.put(Constants.ALERT.ALERT, Constants.ALERT.DANGER);
			resMap.put(Constants.MESSAGE.MESSAGE, Constants.MESSAGE.LOGIN_INVALID);
		}
		
		if ( paramCollector.containsKey("invalid") ) {
			resMap.put(Constants.ALERT.ALERT, Constants.ALERT.DANGER);
			resMap.put(Constants.MESSAGE.MESSAGE, Constants.MESSAGE.SESSION_EXPIRED);
		}
		
		Map<String, ?> fm = RequestContextUtils.getInputFlashMap(paramCollector.getRequest());
		if (fm != null) {
			ResultSetMap voMap = (ResultSetMap) fm.get("vo");
			resMap.put(Constants.ALERT.ALERT, voMap.get(Constants.ALERT.ALERT));
			resMap.put(Constants.MESSAGE.MESSAGE, voMap.get(Constants.MESSAGE.MESSAGE));
		}
		
		if ( (user != null) && (user.getLock() == 0) ) {
			user = null;
			resMap.put(Constants.ALERT.ALERT, Constants.ALERT.WARNNING);
			resMap.put(Constants.MESSAGE.MESSAGE, Constants.MESSAGE.IS_LOCKED);
		}
		
		model.addAttribute("vo", resMap);
		
		if ( user != null ) {
			sUri = "redirect:/admin/manager";
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> rsaPublicMap = (Map<String, Object>) paramCollector.get(Constants.RSA_PUBLIC);
			model.addAllAttributes(rsaPublicMap);
			
			sUri = "admin/login/login";
		}
		
		return sUri;
	}

	@GetMapping("/loginProc")
	public String loginProc(ParamCollector paramCollector, Authentication authentication) {
		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
		logger.debug("user is {}", user);
		
		if (user != null) {
			paramCollector.put(Constants.ID_PWD.USERNAME, user.getUsername());
			loginService.updateLastLoginDt(paramCollector);
		}
		
		StringBuilder sb = new StringBuilder().append("redirect:/admin/manager");
		
		Map<String, ?> fm = RequestContextUtils.getInputFlashMap(paramCollector.getRequest());
		if (fm != null) {
			String sRefererUrl = (String) fm.get("refererUrl");
			sb.setLength(0);
			sb.append("redirect:").append(sRefererUrl);
		}

		return sb.toString();
	}
	
}
