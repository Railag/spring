package com.firrael.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.firrael.spring.data.storage.UserStorage;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    @Qualifier("userDetailsService")
    UserStorage userDetailsService;
	
	@Autowired
	@Qualifier("passwordEncoder")
	PasswordEncoder encoder;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home", "/resources/**", "/register", "/saveUser", "/category", "/favoriteArticle", "/search")
                	.permitAll()
                .antMatchers("/admin",
                		"/articles", "/detailArticle", "/editArticle", "/removeArticle", "/updateArticle",
                		"/users", "/detailUser", "/editUser", "/removeUser", "/updateUser")
                			.hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
            .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        
    	auth
            .inMemoryAuthentication()
                .withUser("u").password("u").roles(Role.ADMIN.toString());
        
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }
}