/**
 * 
 * @author 
 * @version 
 */
package com.dsp.plot.filter;


import com.dsp.plot.service.CustomUserDetailsService;
import com.dsp.plot.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			final String authorizationHeader = request.getHeader("Authorization");

			String username = null;
			String jwt = null;

			log.info("Authorization header: {}", authorizationHeader); // Debug log for Authorization header

			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				jwt = authorizationHeader.substring(7);
				username = jwtUtil.extractUsername(jwt);
				log.debug("JWT token found, username extracted: {}", username);
			} else {
				log.debug("No valid JWT token found in the Authorization header");
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				log.debug("Attempting to load user details for username: {}", username);
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				log.debug("UserDetails loaded for username: {}", username);

				if (userDetails.isEnabled() && jwtUtil.validateToken(jwt, userDetails)) {
					log.debug("JWT token validated successfully for user: {}", username);
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					log.debug("User authenticated successfully: {}", username);
				} else {
					log.error("JWT token validation failed or user is not enabled for username: {}", username);
				}
			} else {
				log.debug("No username found or authentication already set in context");
			}
			chain.doFilter(request, response);
		} catch (JwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage(), e); // Log the exception stack trace
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid JWT token");
		} catch (Exception e) {
			log.error("Error during JWT processing: {}", e.getMessage(), e); // Log the exception stack trace
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("An error occurred while processing the authentication request");
		}
	}
}
