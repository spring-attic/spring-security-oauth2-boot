package sample.secure.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Madhura Bhave
 */
@Configuration
public class AuthenticationConfiguration {

	@Bean
	UserDetailsService userDetailsService() {
		UserDetails greg = User.withDefaultPasswordEncoder()
				.username("greg")
				.password("turnquist")
				.roles("read")
				.build();
		return new InMemoryUserDetailsManager(greg);
	}
}
