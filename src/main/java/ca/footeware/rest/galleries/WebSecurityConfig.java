package ca.footeware.rest.galleries;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Specify the URLs to which the current user can access and those that are
 * forwarded to login.
 *
 * @author Footeware.ca
 */
@Configuration
public class WebSecurityConfig {

	/**
	 * A bean that configures HTTP security.
	 *
	 * @param {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 * @throws Exception when the internet falls over.
	 */
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.cors(withDefaults()).csrf((csrf) -> csrf.disable())
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/galleries/**").allowedOrigins("*").allowedMethods("GET", "POST", "OPTIONS",
						"DELETE", "PUT", "HEAD");
			}
		};
	}
}