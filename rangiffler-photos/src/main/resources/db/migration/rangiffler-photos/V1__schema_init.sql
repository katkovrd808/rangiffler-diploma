CREATE TABLE IF NOT EXISTS photo (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    country_id   UUID NOT NULL,
    description  VARCHAR(255),
    photo        BYTEA,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS photo_like (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    photo_id     UUID NOT NULL,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_like_photo_id
        FOREIGN KEY (photo_id) REFERENCES photo (id) ON DELETE CASCADE,
    UNIQUE (user_id, photo_id)
);

CREATE TABLE IF NOT EXISTS user_country_statistic (
    user_id    UUID NOT NULL,
    country_id UUID NOT NULL,
    count      INTEGER NOT NULL DEFAULT 1 CHECK (count >= 0),
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (user_id, country_id)
);

CREATE INDEX IF NOT EXISTS idx_photo_user_id ON photo (user_id);
CREATE INDEX IF NOT EXISTS idx_photo_country_id ON photo (country_id);
CREATE INDEX IF NOT EXISTS idx_like_photo_id ON photo_like (photo_id);
CREATE INDEX IF NOT EXISTS idx_like_user_id ON photo_like (user_id);

CREATE INDEX IF NOT EXISTS idx_statistic_country_id ON user_country_statistic (country_id);