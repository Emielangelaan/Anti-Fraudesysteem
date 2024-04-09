package antifraud;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final AuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                )
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/shutdown").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/{username}").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                        .requestMatchers("/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.DELETE,"/api/antifraud/suspicious-ip/{ip}").hasRole("SUPPORT")
                        .requestMatchers("/api/antifraud/stolencard").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.DELETE,"/api/antifraud/stolencard/{number}").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/history").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/history/{number}").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole("SUPPORT")
                        .anyRequest().denyAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}