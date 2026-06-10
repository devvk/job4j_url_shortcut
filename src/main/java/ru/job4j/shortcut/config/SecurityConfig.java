package ru.job4j.shortcut.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.job4j.shortcut.filter.JWTAuthenticationFilter;
import ru.job4j.shortcut.filter.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Цепочка фильтров Spring Security.
     * Все HTTP-запросы сначала проходят через неё и только потом попадают в контроллер.
     * Конфигурация:
     * - отключает CSRF для REST API;
     * - запрещает использование HTTP-сессий (STATELESS);
     * - разрешает доступ к /registration и /login без авторизации;
     * - требует JWT для остальных endpoints;
     * - подключает фильтры аутентификации и авторизации JWT.
     *
     * @param http                  объект настройки Spring Security
     * @param authenticationManager менеджер проверки login/password
     * @return настроенная цепочка фильтров Spring Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/registration").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/redirect/**").permitAll()
                        .anyRequest().authenticated())
                .addFilter(new JWTAuthenticationFilter(authenticationManager))
                .addFilter(new JWTAuthorizationFilter(authenticationManager));
        return http.build();
    }

    /**
     * Создаёт AuthenticationManager.
     * AuthenticationManager отвечает за проверку login/password.
     * DaoAuthenticationProvider:
     * - загружает пользователя через UserDetailsService;
     * - получает Bcrypt-хеш пароля из БД;
     * - сравнивает его с паролем из запроса через PasswordEncoder.
     *
     * @return настроенный AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}

