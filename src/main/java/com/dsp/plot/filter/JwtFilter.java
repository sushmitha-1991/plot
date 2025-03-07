/**
 * 
 * @author 
 * @version 
 */
package com.dsp.plot.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dsp.plot.config.ModifiedRequest;
import com.dsp.plot.dto.AuthRequestDTO;
import com.dsp.plot.service.CustomUserDetailsService;
import com.dsp.plot.util.JwtUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${dsp1.authentication.user.ls}")
    private String lsUserName;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RestTemplate restTemplate; // Used to call API for generating token

    // URL to call for token generation
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String username = null;
            String jwt = null;

            log.info("Authorization header received: {}", authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                log.debug("Extracted JWT: {}", jwt);

                if (!jwt.isEmpty()) {
                    try {
                        username = jwtUtil.extractUsername(jwt);
                        log.debug("Valid JWT found for user: {}", username);
                    } catch (JwtException e) {
                        log.warn("Invalid JWT token received, generating new token...");
                        jwt = generateNewToken(); // ✅ Generate new token
                        username = jwtUtil.extractUsername(jwt);
                        if (jwt != null) {
                            // ✅ Explicitly update the request by wrapping it
                            request = new ModifiedRequest(request, "Bearer " + jwt);

                            // ✅ Set in response header
                            response.setHeader("Authorization", "Bearer " + jwt);
                            log.info("New JWT set in response headers.");
                        }
                    }
                }
            } else {
                log.debug("No JWT provided, generating a new token...");
                jwt = generateNewToken();
                username = jwtUtil.extractUsername(jwt);
                if (jwt != null) {
                    // ✅ Explicitly update the request by wrapping it
                    request = new ModifiedRequest(request, "Bearer " + jwt);

                    // ✅ Set in response header
                    response.setHeader("Authorization", "Bearer " + jwt);
                    log.info("New JWT set in response headers.");
                }
            }

            // else {
            // log.error("Failed to generate a new JWT token.");
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // response.getWriter().write("Unable to authenticate. No valid token
            // available.");
            // return;
            // }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("User authenticated successfully: {}", username);
                } else {
                    log.error("JWT Token validation failed for user: {}", username);
                }
            }
            log.info("🟢 Final JWT before chain.doFilter: {}", jwt);
            // ✅ Ensure the wrapped request is passed forward
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error during JWT processing: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                    "{\"error\": \"Server Error\", \"message\": \"An error occurred while processing authentication\"}");
        }
    }

    private String generateNewToken() {
        try {
            AuthRequestDTO authRequest = AuthRequestDTO.builder()
                    .email("testuser5@sansad.nic.in")
                    .otp("123456")
                    .build();
            return jwtUtil.generateToken(lsUserName);

        } catch (Exception e) {
            log.error("Error calling token API: {}", e.getMessage(), e);
        }
        return null;
    }
}
