package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.shortcut.model.Site;
import ru.job4j.shortcut.repository.SiteRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SiteDetailsService implements UserDetailsService {

    private final SiteRepository siteRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String login) throws UsernameNotFoundException {
        Site site = siteRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));
        return User.builder()
                .username(site.getLogin())
                .password(site.getPassword())
                .authorities(List.of())
                .build();
    }
}
