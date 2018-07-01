package kr.co.test.config.spring;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

import common.spring.resolver.ParamMapArgResolver;
import kr.co.test.app.common.spring.util.Jaxb2MarshallerCustom;
import kr.co.test.config.spring.mvc.TilesConfig;
import kr.co.test.config.spring.mvc.ViewConfig;

@Configuration
@Import({
	ViewConfig.class, TilesConfig.class
})
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new ParamMapArgResolver());
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		List<MediaType> stringMediaTypes = new ArrayList<>();
		stringMediaTypes.add(MediaType.TEXT_HTML);
		stringConverter.setSupportedMediaTypes(stringMediaTypes);
		
		converters.add(new MappingJackson2HttpMessageConverter());
		
		MarshallingHttpMessageConverter marshallingConverter = new MarshallingHttpMessageConverter();
		marshallingConverter.setMarshaller( xStreamMarshaller() );
		marshallingConverter.setUnmarshaller( xStreamMarshaller() );
	}
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		// TODO : Spring Boot 에서 beanName 동작 안함
		registry.beanName();
		registry.tiles();
	}
	
	/**
	 * HttpMessageConverter 참조 : Jaxb 의 @XmlRootElement 무시, classpath 로 표기
	 * @return
	 */
	@Bean
	public XStreamMarshaller xStreamMarshaller() {
		XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
		xStreamMarshaller.setAutodetectAnnotations(true);
		return xStreamMarshaller;
	}
	
	/**
	 * HttpMessageConverter 참조 : Xstream @XStreamAlias 인식 불가
	 * @return
	 */
	@Bean
	public Jaxb2Marshaller jaxbMarshaller() {
		Jaxb2MarshallerCustom jaxb2Custom = new Jaxb2MarshallerCustom();
		return jaxb2Custom.jaxb2Marshaller("kr.co.test.app");
	}

	@Bean
	public SessionLocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("ko"));
		return new SessionLocaleResolver();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		
		registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/**");
	}
	
	@Bean
	public MarshallingView xstreamView() {
		MarshallingView xstreamView = new MarshallingView();
		xstreamView.setMarshaller( xStreamMarshaller() );
		xstreamView.setModelKey("xmlData");
		return xstreamView;
	}
	
	@Bean
	public MarshallingView jaxbView() {
		MarshallingView jaxbView = new MarshallingView();
		jaxbView.setMarshaller( jaxbMarshaller() );
		jaxbView.setModelKey("xmlData");
		return jaxbView;
	}
	
	@Bean
	public ContentNegotiatingViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
		ContentNegotiatingViewResolver cnv = new ContentNegotiatingViewResolver();
		
		cnv.setContentNegotiationManager( manager );
		cnv.setDefaultViews(this.defaultViews());
		cnv.setOrder(1);
		return cnv;
	}
	
	private List<View> defaultViews() {
		List<View> defaultViews = new ArrayList<>();
		defaultViews.add(new MappingJackson2JsonView());
		defaultViews.add(new MarshallingView(xStreamMarshaller()));
		return defaultViews;
	}
	
}
