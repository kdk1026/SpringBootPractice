package kr.co.test.config.spring.app;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Import({
	TransactionalConfig.class
})
public class DataSourceConfig {

	@Bean(name = "h2DataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.h2_datasource")
	public DataSource datasource() {
        return DataSourceBuilder.create().build();
	}


}
