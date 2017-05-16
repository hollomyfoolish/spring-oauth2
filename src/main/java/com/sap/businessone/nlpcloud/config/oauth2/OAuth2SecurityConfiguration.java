package com.sap.businessone.nlpcloud.config.oauth2;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
    private ClientDetailsService clientDetailsService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
        	.withUser("admin").password("admin").roles("ADMIN")
        .and()
        	.withUser("cc").password("cc").roles("USER");
	 }
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .csrf().disable()
        .anonymous().disable()
        .authorizeRequests()
        .antMatchers("/oauth/token").permitAll();
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public TokenStore tokenStore() {
    	return new JdbcTokenStore(dataSource);
    }
}
