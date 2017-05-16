package com.sap.businessone.nlpcloud.config.db;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jdbc.properties")
public class NlpDataSourceConfig {
	
	protected static final int DEFAULT_MinEvictableIdleTimeMillis    = 1000;
	protected static final int DEFAULT_TimeBetweenEvictionRunsMillis = 40000;
	
	@Bean
	public DataSource nlpDataSource(@Value("${driver.class}")String driver, @Value("${url}")String url,
			@Value("${user.name}")String userName, @Value("${password}")String password,
			@Value("${max.active}")int maxActive, @Value("${max.idle}")int maxIdle, @Value("${max.wait}")int maxWait){
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxIdle(maxIdle);
		dataSource.setMaxWait(maxWait);
		
		//disable "TestOnBorrow" due to the performance concern
		//dataSource.setTestOnBorrow(true);
		//dataSource.setTestOnReturn(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setMinEvictableIdleTimeMillis(DEFAULT_MinEvictableIdleTimeMillis);
		dataSource.setTimeBetweenEvictionRunsMillis(DEFAULT_TimeBetweenEvictionRunsMillis);
		dataSource.setNumTestsPerEvictionRun(10);
		
		dataSource.setTestOnBorrow(true);
		dataSource.setValidationQuery("select 1 from dummy");
		dataSource.setJdbcInterceptors(StatementFinalizer.class.getName());
		
		return dataSource;
	}
	
}
