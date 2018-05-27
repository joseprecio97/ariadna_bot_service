package com.joseprecio.projectefinalcurs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${auth.username}")
	private String username;
	
	@Value("${auth.password}")
	private String password;
	
	/**
	 * Configuración de seguridad
	 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/chat", "/images/**", "/ariadna_bot_service/**", "/vendor/**", "/dist/**", "/login", "/api/v1/command", "/api/v1/googleassistant").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .successForwardUrl("/loginok")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
            .csrf()
            	.disable();
    }

    /**
     * Autenticación
     * 
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
         .inMemoryAuthentication()
          .withUser(username).password(password).roles("USER");
    }
    
}