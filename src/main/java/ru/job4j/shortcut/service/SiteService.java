package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.job4j.shortcut.dto.RegistrationRequestDto;
import ru.job4j.shortcut.dto.RegistrationResponseDto;
import ru.job4j.shortcut.dto.StatisticResponseDto;
import ru.job4j.shortcut.model.Site;
import ru.job4j.shortcut.repository.SiteRepository;
import ru.job4j.shortcut.repository.UrlRepository;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final UrlRepository urlRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResponseDto create(RegistrationRequestDto requestDto) {
        if (siteRepository.findByDomain(requestDto.site()).isPresent()) {
            return new RegistrationResponseDto(false, null, null);
        }
        String credentials = UUID.randomUUID().toString().substring(0, 5);
        Site newSite = new Site();
        newSite.setDomain(requestDto.site());
        newSite.setLogin(credentials);
        newSite.setPassword(passwordEncoder.encode(credentials));

        try {
            siteRepository.save(newSite);
            return new RegistrationResponseDto(true, credentials, credentials);
        } catch (DataIntegrityViolationException ex) {
            return new RegistrationResponseDto(false, null, null);
        }
    }

    public List<StatisticResponseDto> getStatistic(String login) {
        Site site = siteRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));
        return urlRepository.findAllBySite(site).stream()
                .map(url -> new StatisticResponseDto(url.getOriginalUrl(), url.getVisitCount()))
                .toList();
    }
}
