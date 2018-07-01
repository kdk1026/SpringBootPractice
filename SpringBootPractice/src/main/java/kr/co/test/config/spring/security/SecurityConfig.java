package kr.co.test.config.spring.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import kr.co.test.app.page.login.security.PageAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// TODO : SpringBoot Security 이슈 1개만 인식되는 듯
//	@Order(1)
//	@Configuration
//	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
//		
//		public ApiWebSecurityConfigurationAdapter() {
//			super();
//		}
//		
//		@Override
//		public void configure(WebSecurity web) throws Exception {
//			web.ignoring()
//				.antMatchers("/api/login/auth");
//		}
//		
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http.antMatcher("/api/**")
//				.authorizeRequests()
//				.anyRequest().authenticated();			
//			
//			http.sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//			
//			http.headers()
//				.cacheControl();
//
//			http.csrf().disable();
//
//			http.formLogin().disable();
//
//			http.logout()
//				.logoutUrl("/api/logout")
//				.logoutSuccessHandler(new RestLogoutSuccessHandler());
//
//			http.exceptionHandling()
//				.authenticationEntryPoint(new RestAuthenticationEntryPoint())
//				.accessDeniedHandler(new RestAccessDeniedHandler());
//
//			http.addFilterBefore( this.jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class );
//			
//			http.cors().configurationSource(RestCorsConfig.configurationSource());
//		}
//		
//		@Override
//		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//			auth.authenticationProvider(new JwtAuthenticationProvider());
//		}
//		
//		@Bean
//		public JwtAuthenticationTokenFilter jwtAuthenticationFilter() throws Exception {
//			JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
//			
//			jwtAuthenticationTokenFilter.setAuthenticationManager(this.authenticationManager());
//			jwtAuthenticationTokenFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
//			return jwtAuthenticationTokenFilter;
//		}
//	}
	
	@Order(2)
	@Configuration
	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		public FormLoginWebSecurityConfigurerAdapter() {
			super();
		}

		private static final String LOGIN_PAGE = "/admin/login";
		
		@Autowired @Qualifier("h2DataSource")
        private DataSource h2DataSource;
		
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring()
				.antMatchers("/resources/**")
				.antMatchers("/webjars/**")
				.antMatchers("/console/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.headers()
				.cacheControl()
				.and()
					.contentTypeOptions()
				.and()
					.httpStrictTransportSecurity()
						.includeSubDomains(true)
						.maxAgeInSeconds(31536000)
				.and()
					.frameOptions().sameOrigin()
					.xssProtection().block(false);

			http.csrf();

			http.authorizeRequests()
				.antMatchers("/**", LOGIN_PAGE).permitAll()
				.antMatchers("/admin/**").authenticated()
				.antMatchers("/admin/managerRemoveProcess").hasRole("ADMIN");
			
			// XXX: 로그인 처리만 https로 하는 경우
			/*
			http.requiresChannel()
				.antMatchers("/admin/loginProc").requiresSecure()
				.antMatchers("/**").requiresInsecure();
			*/

			http.formLogin()
				.loginPage(LOGIN_PAGE)
				.defaultSuccessUrl("/admin/login/loginProc")
				.failureUrl("/admin/login?invalid=Y")
				.loginProcessingUrl("/admin/login/auth")
				.usernameParameter("username").passwordParameter("password");

			// XXX: 404 오류로 Controller 수작업 (Java 6, GET) - Spring 4 미확인
			/*
			http.logout()
				.logoutUrl("/admin_logout")
				.logoutSuccessUrl("/admin/login?isTimeOut=Y")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true);
			*/	

			http.rememberMe()
				.rememberMeParameter("remember-me")
				.key("steady")
				.tokenValiditySeconds(24*60*60)	// 86400
				.tokenRepository( persistentTokenRepository() );

			http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.invalidSessionUrl(LOGIN_PAGE)
				.sessionFixation().none()
				.maximumSessions(2)
					.expiredUrl(LOGIN_PAGE)
					.maxSessionsPreventsLogin(true);
		}

		@Bean
		public PersistentTokenRepository persistentTokenRepository() {
			JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
			db.setDataSource(h2DataSource);
			return db;
		}
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(new PageAuthenticationProvider());
		}
	}

}
