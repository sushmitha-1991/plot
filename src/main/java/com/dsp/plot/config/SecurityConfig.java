/**
 * 
 * @author 
 * @version 
 */
package com.dsp.plot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dsp.plot.exception.CustomAccessDeniedHandler;
import com.dsp.plot.filter.JwtFilter;
import com.dsp.plot.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public UserDetailsService userDetailsService() {
		log.debug("Creating UserDetailsService bean");
		return new CustomUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		log.debug("Creating PasswordEncoder bean");
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		log.debug("Creating AuthenticationProvider bean");
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		log.debug("Creating AuthenticationManager bean");
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		log.debug("Configuring SecurityFilterChain");
		http.authorizeHttpRequests((authz) -> authz.requestMatchers(
				// "plot-input/**",
				"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/ltno/**").permitAll().requestMatchers("/**")
				.hasRole("ADMIN").anyRequest().authenticated()).csrf(AbstractHttpConfigurer::disable)
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		http.exceptionHandling((ex) -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
				.accessDeniedHandler(customAccessDeniedHandler))
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		log.debug("SecurityFilterChain configured successfully");
		return http.build();
	}
}
