package br.com.forum_hub.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {

        @Bean
        public SecurityFilterChain filtrosSeguranca(HttpSecurity http) throws Exception{
                return http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).csrf(csrf -> csrf.disable()).build();
        }

        @Bean
        public PasswordEncoder encriptador(){
                return new BCryptPasswordEncoder();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring().requestMatchers("/h2-console/**");
        }

}
