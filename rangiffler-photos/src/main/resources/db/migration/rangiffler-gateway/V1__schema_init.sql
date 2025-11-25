CREATE TABLE IF NOT EXISTS "photo"
(
    id                      UUID        UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    user_id                 UUID               NOT NULL,
    country_id              UUID               NOT NULL,
    description             varchar(255),
    photo                   bytea,
    created_date            date               NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "like"
(
    id                      UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    user_id                 UUID        NOT NULL,
    created_date            date        NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "photo_like"
(
    photo_id                 UUID        NOT NULL,
    like_id                  UUID        NOT NULL,
    PRIMARY KEY (photo_id, like_id),
    CONSTRAINT ph_like_photo_id FOREIGN KEY (photo_id) REFERENCES "photo" (id),
    CONSTRAINT lk_like_photo_id FOREIGN KEY (like_id) REFERENCES "like" (id)
);

CREATE TABLE IF NOT EXISTS "statistic"
(
    id                      UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    user_id                 UUID        NOT NULL,
    country_id              UUID        NOT NULL,
    count                   int         NOT NULL,
    PRIMARY KEY (id)
);