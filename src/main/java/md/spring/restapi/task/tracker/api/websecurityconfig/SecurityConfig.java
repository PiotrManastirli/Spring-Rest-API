package md.spring.restapi.task.tracker.api.websecurityconfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        String usersByUsernameQuery = "SELECT username, password, true as enabled FROM users WHERE username = ?";
        String authoritiesByUsernameQuery = "SELECT username, role FROM users WHERE username = ?";
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(usersByUsernameQuery);
        manager.setAuthoritiesByUsernameQuery(authoritiesByUsernameQuery);
        return manager;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/swagger-ui/index.html#/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .successHandler((request, response, authentication) -> response.sendRedirect("/swagger-ui/index.html#/"))
                );
        return http.build();
    }

}


