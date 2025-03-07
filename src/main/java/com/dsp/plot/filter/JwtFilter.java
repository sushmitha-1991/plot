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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
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

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RestTemplate restTemplate; // Used to call API for generating token

    @Value("${auth.token.api}")
    private String authTokenApi; // URL to call for token generation

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String username = null;
            String jwt = null;
            log.info("Authorization header: {}", authorizationHeader);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
                log.debug("JWT token found, username extracted: {}", username);
            } else {
                log.debug("No valid JWT token found, attempting to generate a new one...");
                jwt = generateNewToken();
                System.out.println("!!!!");
                System.out.println(jwt);
                if (jwt != null) {
                    log.debug("New JWT token obtained: {}", jwt);
                    // Manually set the token in the request header
                    request.setAttribute("Authorization", "Bearer " + jwt);
                    // Also set in response header for client usage
                    response.setHeader("Authorization", "Bearer " + jwt);
                    // Send new token in response header
                    String testToken = (String) request.getAttribute("Authorization");
                    log.info("Token stored in request attribute: {}", testToken);
                } else {
                    log.error("Failed to obtain a new JWT token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unable to authenticate. No valid token available.");
                    return;
                }
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Attempting to load user details for username: {}", username);

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("userDetails");
                System.out.println(userDetails);
                if (userDetails.isEnabled() && jwtUtil.validateToken(jwt, userDetails)) {
                    log.debug("JWT token validated successfully for user: {}", username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                        System.out.println("auth");
                        System.out.println(auth);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.debug("User authenticated successfully: {}", username);
                } else {
                    log.error("JWT token validation failed or user is not enabl4Red for username: {}", username);
                }
            }

            chain.doFilter(request, response);
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
        } catch (Exception e) {
            log.error("Error during JWT processing: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing authentication");
        }
    }

    private String generateNewToken() {
        try {
            AuthRequestDTO authRequest = AuthRequestDTO.builder()
                    .email("testuser5@sansad.nic.in")
                    .otp("123456")
                    .build();
            log.info("Sending authentication request: {}", authRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuthRequestDTO> requestEntity = new HttpEntity<>(authRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(authTokenApi, requestEntity, String.class);
            log.info("Token API Response: {}", response);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Generated Token: {}", response.getBody()); // ✅ Print the token
                return response.getBody();
            } else {
                log.error("Failed to get new token, status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error calling token API: {}", e.getMessage(), e);
        }
        return null;
    }
}
// @Component
// public class JwtFilter extends OncePerRequestFilter {

// private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

// @Autowired
// private JwtUtil jwtUtil;

// @Autowired
// private CustomUserDetailsService userDetailsService;

// @Override
// protected void doFilterInternal(HttpServletRequest request,
// HttpServletResponse response, FilterChain chain)
// throws ServletException, IOException {
// try {
// final String authorizationHeader = request.getHeader("Authorization");

// String username = null;
// String jwt = null;

// log.info("Authorization header: {}", authorizationHeader); // Debug log for
// Authorization header

// if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
// {
// jwt = authorizationHeader.substring(7);
// username = jwtUtil.extractUsername(jwt);
// log.debug("JWT token found, username extracted: {}", username);
// } else {
// log.debug("No valid JWT token found in the Authorization header");
// }

// if (username != null &&
// SecurityContextHolder.getContext().getAuthentication() == null) {
// log.debug("Attempting to load user details for username: {}", username);
// UserDetails userDetails =
// this.userDetailsService.loadUserByUsername(username);
// log.debug("UserDetails loaded for username: {}", username);

// if (userDetails.isEnabled() && jwtUtil.validateToken(jwt, userDetails)) {
// log.debug("JWT token validated successfully for user: {}", username);
// UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
// UsernamePasswordAuthenticationToken(
// userDetails, null, userDetails.getAuthorities());
// usernamePasswordAuthenticationToken
// .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
// SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
// log.debug("User authenticated successfully: {}", username);
// } else {
// log.error("JWT token validation failed or user is not enabled for username:
// {}", username);
// }
// } else {
// log.debug("No username found or authentication already set in context");
// }
// chain.doFilter(request, response);
// } catch (JwtException e) {
// log.error("Invalid JWT token: {}", e.getMessage(), e); // Log the exception
// stack trace
// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
// response.getWriter().write("Invalid JWT token");
// } catch (Exception e) {
// log.error("Error during JWT processing: {}", e.getMessage(), e); // Log the
// exception stack trace
// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
// response.getWriter().write("An error occurred while processing the
// authentication request");
// }
// }
// }
