package ca.footeware.rest.recipes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
     * @param  {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception when the internet falls over.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeHttpRequests().requestMatchers("/recipes/**").hasRole("USER").and()
                .httpBasic();
        return http.build();
    }

    /**
     * Use BCrypt for password encoding.
     *
     * @return {@link PasswordEncoder}
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Username and password.
     *
     * @return {@link InMemoryUserDetailsManager}
     */
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("craig").password(passwordEncoder().encode("chocolate")).roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/recipes/**").allowedOrigins("*").allowedMethods("GET", "POST", "OPTIONS",
                        "DELETE", "PUT");
            }
        };
    }
}