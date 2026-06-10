package ru.job4j.shortcut.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequestDto(

        @NotBlank(message = "site must not be blank")
        String site
) {
}
