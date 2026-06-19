package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UrlService {

    private static final int MAX_CODE_GENERATION_ATTEMPTS = 3;
    private final SiteRepository siteRepository;
    private final UrlRepository urlRepository;

    public ConvertResponseDto convert(ConvertRequestDto requestDto, String login) {
        Site site = siteRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException(login));

        Optional<Url> existingUrl = urlRepository.findBySiteAndOriginalUrl(site, requestDto.url());
        if (existingUrl.isPresent()) {
            return new ConvertResponseDto(existingUrl.get().getShortCode());
        }

        for (int i = 0; i < MAX_CODE_GENERATION_ATTEMPTS; i++) {
            String code = generateUniqueCode();
            Url url = new Url();
            url.setShortCode(code);
            url.setOriginalUrl(requestDto.url());
            url.setSite(site);

            try {
                urlRepository.saveAndFlush(url);
                return new ConvertResponseDto(code);
            } catch (DataIntegrityViolationException e) {
                Optional<Url> urlCreatedByAnotherThread = urlRepository.findBySiteAndOriginalUrl(site, requestDto.url());
                // если это был дубль урл
                if (urlCreatedByAnotherThread.isPresent()) {
                    return new ConvertResponseDto(urlCreatedByAnotherThread.get().getShortCode());
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not generate unique short code.");
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 7);
        } while (urlRepository.findByShortCode(code).isPresent());
        return code;
    }

    @Transactional
    public String redirect(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found"));
        urlRepository.incrementVisitCount(shortCode);
        return url.getOriginalUrl();
    }
}
