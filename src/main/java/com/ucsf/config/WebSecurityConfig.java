package com.ucsf.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UcsfAuthenticationEntryPoint ucsfAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService ucsfUserDetailsService;

	@Autowired
	private UcsfRequestFilter ucsfRequestFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(ucsfUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// We don't need CSRF for this example
		httpSecurity.csrf().disable().cors().configurationSource(corsConfigurationSource()).and()
				// dont authenticate this particular request
				.authorizeRequests().antMatchers("/api/auth/**").permitAll().antMatchers("/api/verify").permitAll()
				.antMatchers("/swagger-ui/index.html").permitAll()
				.antMatchers("/swagger-ui/**").permitAll()
				.antMatchers("/**").permitAll()
				.antMatchers("/swagger-ui.html").permitAll()
				.antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/configuration/**").permitAll()		
				.antMatchers("/v2/**").permitAll()
				.antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/api/password/**").permitAll().antMatchers("/api/questions/**").hasRole("PATIENT")
				.antMatchers("/api/answers/**").hasRole("PATIENT").antMatchers("/api/study/**").hasRole("PATIENT")
				.antMatchers("/api/survey/**").hasRole("PATIENT").antMatchers("/api/users/**").hasRole("ADMIN").anyRequest()
				.authenticated().and().

				// make sure we use stateless session; session won't be used to
				// store user's state.
				exceptionHandling().authenticationEntryPoint(ucsfAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(ucsfRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
	   CorsConfigurationSource corsConfigurationSource() {
	       CorsConfiguration configuration = new CorsConfiguration();
	       configuration.setAllowedOrigins(Arrays.asList("*"));
	       configuration.setAllowedMethods(Arrays.asList("GET","POST"));
	      // configuration.setAllowCredentials(true);
	       //the below three lines will add the relevant CORS response headers
	       configuration.addAllowedOrigin("*");
	       configuration.addAllowedHeader("*");
	       configuration.addAllowedMethod("*");
	       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	       source.registerCorsConfiguration("/**", configuration);
	       return source;
	   }
}
