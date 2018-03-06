package com.innotree.smartkms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	  public void configureGlobal(AuthenticationManagerBuilder auth) {
	    try {
			auth
			  .inMemoryAuthentication()
			    .withUser("mazdah").password("w00hj8928").roles("ADMIN");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
}
