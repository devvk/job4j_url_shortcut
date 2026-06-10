package ru.job4j.shortcut.filter;

import com.auth0.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.job4j.shortcut.model.Site;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static ru.job4j.shortcut.filter.JWTAuthorizationFilter.*;

/**
 * Фильтр аутентификации JWT слушает POST /login.
 * Фильтр читает login/password из JSON, передаёт их в AuthenticationManager
 * и при успешной проверке возвращает JWT в заголовке Authorization.
 */
@AllArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * Срок действия JWT: 10 дней.
     */
    private static final long EXPIRATION_TIME = 864_000_000;
    private final AuthenticationManager authenticationManager;

    /**
     * Читает тело запроса логина и запускает проверку login/password.
     * В JSON ожидается объект Site. Из него берутся login и password.
     * Затем создаётся UsernamePasswordAuthenticationToken, который передаётся
     * в AuthenticationManager. Сам менеджер дальше вызывает UserDetailsService
     * и PasswordEncoder для проверки пользователя.
     *
     * @param request  HTTP-запрос с JSON login/password
     * @param response HTTP-ответ
     * @return результат успешной аутентификации
     * @throws AuthenticationException если login/password неверные
     */
    @Override
    public @NonNull Authentication attemptAuthentication(HttpServletRequest request,
                                                         @NonNull HttpServletResponse response) throws AuthenticationException {
        try {
            Site credentials = new ObjectMapper()
                    .readValue(request.getInputStream(), Site.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    credentials.getLogin(),
                    credentials.getPassword(),
                    Collections.emptyList()
            );
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Вызывается после успешной проверки login/password.
     * Создаёт JWT, кладёт login пользователя в subject, добавляет срок действия
     * через exp и возвращает токен клиенту в заголовке Authorization.
     *
     * @param request        HTTP-запрос
     * @param response       HTTP-ответ, в который добавляется JWT
     * @param chain          цепочка фильтров
     * @param authentication результат успешной аутентификации
     */
    @Override
    protected void successfulAuthentication(@NonNull HttpServletRequest request,
                                            @NonNull HttpServletResponse response,
                                            @NonNull FilterChain chain,
                                            Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Unexpected principal type");
        }
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
