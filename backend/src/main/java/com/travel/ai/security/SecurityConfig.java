package com.travel.ai.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/scenic/**").permitAll()
            .antMatchers("/api/map/**").permitAll()
            .antMatchers("/api/comment/**").permitAll()
            .antMatchers("/api/ai/**").permitAll()
            .antMatchers("/api/upload/**").permitAll()
            .antMatchers("/uploads/**").permitAll()
            .antMatchers("/api/strategy/**").permitAll()
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}