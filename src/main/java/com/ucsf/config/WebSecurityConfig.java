package com.ucsf.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${aws-accessKey}")
    private String accessKey;
	
    @Value("${aws-secretKey}")
    private String secretKey;

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
				.antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/configuration/**").permitAll()		
				.antMatchers("/v2/**").permitAll()
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
	
	@Bean
	AmazonS3 initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials( accessKey, secretKey);
		AmazonS3 s3client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.US_WEST_2)
				.build();
		return s3client;
    }
	
	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
	    GoogleCredentials googleCredentials = GoogleCredentials
	            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
	    FirebaseOptions firebaseOptions = FirebaseOptions
	            .builder()
	            .setCredentials(googleCredentials)
	            .build();
	    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "com.SkinTracker");
	    return FirebaseMessaging.getInstance(app);
	}
}
