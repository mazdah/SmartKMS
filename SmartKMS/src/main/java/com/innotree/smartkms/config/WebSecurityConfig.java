package com.innotree.smartkms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = {"com.innotree.smartkms"})
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		super.configure(web);
		
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		super.configure(http);
		
		http
		.headers().frameOptions().sameOrigin().and() // iframe 허용 처리
		.authorizeRequests().antMatchers("/SmartKMS/admin/**").access("ROLE_ADMIN");
	}
	
	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub	
		auth.inMemoryAuthentication()
		.withUser("admin")
		.password("{noop}admin")
		.roles("ADMIN");
	}

}
