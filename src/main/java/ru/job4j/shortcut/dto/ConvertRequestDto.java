package ru.job4j.shortcut.dto;

import jakarta.validation.constraints.NotBlank;

public record ConvertRequestDto(

        @NotBlank(message = "url must not be blank")
        String url
) {
}
