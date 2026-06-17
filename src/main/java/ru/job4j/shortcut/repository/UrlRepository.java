package ru.job4j.shortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.job4j.shortcut.model.Site;
import ru.job4j.shortcut.model.Url;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Integer> {

    Optional<Url> findByShortCode(String code);

    List<Url> findAllBySite(Site site);

    @Modifying
    @Query("UPDATE Url u SET u.visitCount = u.visitCount + 1 WHERE u.shortCode = :code")
    void incrementVisitCount(String code);

    Optional<Url> findBySiteAndOriginalUrl(Site site, String originalUrl);
}
