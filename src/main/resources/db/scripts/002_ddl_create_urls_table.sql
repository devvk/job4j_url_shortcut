--liquibase formatted sql

--changeset devvk:002_ddl_create_urls_table
CREATE TABLE urls
(
    id           SERIAL PRIMARY KEY,
    short_code   VARCHAR(20) UNIQUE NOT NULL,
    original_url VARCHAR(255)       NOT NULL,
    visit_count  INTEGER            NOT NULL DEFAULT 0,
    site_id      INTEGER            NOT NULL REFERENCES sites (id),
    created_at   TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE urls;
