--liquibase formatted sql

--changeset devvk:001_ddl_create_sites_table
CREATE TABLE sites
(
    id         SERIAL PRIMARY KEY,
    domain     VARCHAR(255) UNIQUE NOT NULL,
    login      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE sites;
