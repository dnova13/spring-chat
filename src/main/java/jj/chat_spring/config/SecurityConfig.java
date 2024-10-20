package jj.chat_spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // CSRF 보호 활성화
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        // custom 셋팅
        // http.csrf(Customizer.withDefaults());

        
        // 특정 경로를 제외한 csrf 셋팅
        http
            .csrf(c -> c.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());  // 그 외의 경로는 인증없이 모두 허용
//            .cors((cors) -> cors
//                    .configurationSource(myWebsiteConfigurationSource())
//            )


        // csrf 비활성화
        /*http
        .csrf(csrf -> csrf.disable()) // CSRF 비활성화
        .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
        );*/
        

        return http.build();
    }
}