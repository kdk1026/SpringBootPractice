package kr.co.test.config.spring;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;

import common.servlet.filter.CorsFilter;
import common.servlet.filter.SessionCookieFilter;
import kr.co.test.config.SiteMeshFilter;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean getXssEscapeServletFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new XssEscapeServletFilter());
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean getSiteMeshFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new SiteMeshFilter());
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean getSessionCookieFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new SessionCookieFilter());
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean getCorsFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CorsFilter());
		registrationBean.addUrlPatterns("/api/*");
		return registrationBean;
	}
	
}
