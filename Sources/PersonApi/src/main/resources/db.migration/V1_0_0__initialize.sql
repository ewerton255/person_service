CREATE TABLE IF NOT EXISTS person
(
    id             serial      NOT NULL,
    "name"         varchar(64) NOT NULL,
    email          varchar(32) NOT NULL,
    dt_creation    timestamp   NOT NULL,
    dt_last_update timestamp NULL,
    CONSTRAINT person_pkey PRIMARY KEY (id)
);