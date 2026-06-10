package ru.job4j.shortcut.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.shortcut.dto.RegistrationRequestDto;
import ru.job4j.shortcut.dto.RegistrationResponseDto;
import ru.job4j.shortcut.dto.StatisticResponseDto;
import ru.job4j.shortcut.service.SiteService;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class SiteController {

    private final SiteService siteService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponseDto> registration(@Valid @RequestBody RegistrationRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(siteService.create(requestDto));
    }

    @GetMapping("/statistic")
    public List<StatisticResponseDto> getStatistic(Principal principal) {
        return siteService.getStatistic(principal.getName());
    }
}
