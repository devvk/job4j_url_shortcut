package ru.job4j.shortcut.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.shortcut.dto.ConvertRequestDto;
import ru.job4j.shortcut.dto.ConvertResponseDto;
import ru.job4j.shortcut.service.UrlService;

import java.net.URI;
import java.security.Principal;

@RestController
@AllArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/convert")
    public ResponseEntity<ConvertResponseDto> convert(
            @Valid @RequestBody ConvertRequestDto requestDto,
            Principal principal) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(urlService.convert(requestDto, principal.getName()));
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = urlService.redirect(code);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
