package kr.co.test.config.spring;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;

import kr.co.test.app.common.spring.support.DatabaseMessageSource;
import kr.co.test.app.page.locale.service.LocaleServiceImpl;
import kr.co.test.config.spring.app.DataSourceConfig;
import kr.co.test.config.spring.app.TaskConfig;

@Configuration
@Import({
	DataSourceConfig.class, TaskConfig.class
})
public class AppConfig {

	@Bean
	public PropertiesFactoryBean file() {
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		properties.setLocation(new ClassPathResource("properties/file.properties"));
		return properties;
	}
	@Bean
	public PropertiesFactoryBean jwt() {
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		properties.setLocation(new ClassPathResource("properties/jwt.properties"));
		return properties;
	}

	@Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:locale/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        messageSource.setCacheSeconds(0);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
	
	@Bean
	public DatabaseMessageSource databaseMessageSource() {
		DatabaseMessageSource databaseMessageSource = new DatabaseMessageSource();
		databaseMessageSource.setMessages(new LocaleServiceImpl());
		return databaseMessageSource;
	}

}
