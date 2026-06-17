package ru.job4j.shortcut.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotBlank(message = "short code must not be empty")
    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    @NotBlank(message = "original url must not be empty")
    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "visit_count", nullable = false)
    private Integer visitCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;
}
