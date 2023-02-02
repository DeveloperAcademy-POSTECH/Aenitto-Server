package com.firefighter.aenitto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.auth.JwtAuthenticationFilter;
import com.firefighter.aenitto.auth.service.TokenService;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	// @Bean
	// public WebSecurityCustomizer webSecurityCustomizer() {
	//     return (web) -> web.ignoring().antMatchers("/static/**");
	// }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, TokenService tokenService) throws
		Exception {

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenService);

		http
			.authorizeRequests()
			.antMatchers(
				"/api/v1/auth/**"
				, "/api/v1/temp-login"
				, "/api/v1/login"
				, "/api/v2/login"
				, "/static/**"
			).permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.disable()
			.csrf()
			.disable()
			.headers()
			.disable()
			.httpBasic()
			.disable()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
