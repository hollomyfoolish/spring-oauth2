package com.sap.businessone.nlpcloud.config.oauth2;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
public class NlpCloudOAuth2ServerConfig {
	private static final String RESOURCE_ID = "nlp_oauth_user_info";
	
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		@Override
	    public void configure(ResourceServerSecurityConfigurer resources) {
	        resources.resourceId(RESOURCE_ID).stateless(false);
	    }
		
		@Override
	    public void configure(HttpSecurity http) throws Exception {
	        http
	        	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	        .and()	
	        	.anonymous().disable()
	        	.requestMatchers().antMatchers("/user/**")
	        .and()
	        	.authorizeRequests()
	        		.antMatchers("/user/**").access("hasRole('ADMIN')")
	        .and()
	        	.exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	    }
	}
	
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
		private static String REALM="NLP_CLOUD_OAUTH_REALM";
		@Autowired
		private DataSource dataSource;
		
		@Autowired
	    private TokenStore tokenStore; 
		
		@Autowired
		private UserApprovalHandler userApprovalHandler;
		
		@Autowired
		private ClientDetailsService clientDetailsService;
		
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.jdbc(dataSource);
		}
		
		@Override
	    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
	                .authenticationManager(authenticationManager);
	    }
		
		@Override
	    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
	        oauthServer.realm(REALM + "/client");
	    }
		
		@Bean
		public ApprovalStore approvalStore() throws Exception {
			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore);
			return store;
		}
		
		@Bean
		@Lazy
		@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
		public NlpCloudUserApprovalHandler userApprovalHandler() throws Exception {
			NlpCloudUserApprovalHandler handler = new NlpCloudUserApprovalHandler();
			handler.setApprovalStore(approvalStore());
			handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
			handler.setClientDetailsService(clientDetailsService);
			handler.setUseApprovalStore(true);
			return handler;
		}
		
	}
}
