package com.pfc;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(a -> a
   .requestMatchers("/login", "/error", "/api/auth/csrf", "/css/**", "/js/**", "/images/**").permitAll()
   .requestMatchers("/api/usuarios/**", "/api/clientes/**", "/clientes", "/api/ajustes-horarios/**", "/ajustes-horarios/**").hasRole("ADMIN")
   .anyRequest().authenticated())
   .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
   .logout(l -> l.logoutSuccessUrl("/login?logout"))
   .exceptionHandling(e -> e.defaultAuthenticationEntryPointFor((req,res,ex) -> {
    res.setStatus(401); res.setContentType("application/json;charset=UTF-8");
    res.getWriter().write("{\"mensagem\":\"Autenticação necessária\"}");
   }, new AntPathRequestMatcher("/api/**")));
  return http.build();
 }
}
