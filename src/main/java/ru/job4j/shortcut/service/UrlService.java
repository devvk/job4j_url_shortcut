package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.shortcut.dto.ConvertRequestDto;
import ru.job4j.shortcut.dto.ConvertResponseDto;
import ru.job4j.shortcut.model.Site;
import ru.job4j.shortcut.model.Url;
import ru.job4j.shortcut.repository.SiteRepository;
import ru.job4j.shortcut.repository.UrlRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UrlService {

    private final SiteRepository siteRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public ConvertResponseDto convert(ConvertRequestDto requestDto, String login) {
        Site site = siteRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));
        String code = generateUniqueCode();
        Url url = new Url();
        url.setShortCode(code);
        url.setOriginalUrl(requestDto.url());
        url.setSite(site);
        urlRepository.save(url);
        return new ConvertResponseDto(code);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 7);
        } while (urlRepository.findByShortCode(code).isPresent());
        return code;
    }

    @Transactional
    public String redirect(String code) {
        Url url = urlRepository.findByShortCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found"));
        urlRepository.incrementVisitCount(code);
        return url.getOriginalUrl();
    }
}
