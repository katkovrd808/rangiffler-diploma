CREATE TABLE IF NOT EXISTS "user"
(
    id                      UUID        UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    username                varchar(50) UNIQUE NOT NULL,
    firstname               varchar(255),
    surname                 varchar(255),
    photo                   bytea,
    country_id              UUID               NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "friendship"
(
    requester_id UUID          NOT NULL,
    addressee_id UUID          NOT NULL,
    created_date date          NOT NULL DEFAULT CURRENT_DATE,
    status varchar(50)         NOT NULL,
    PRIMARY KEY (requester_id, addressee_id),
    CONSTRAINT friend_are_distinct_ck CHECK (requester_id <> addressee_id),
    CONSTRAINT fk_requester_id FOREIGN KEY (requester_id) REFERENCES "user" (id),
    CONSTRAINT fk_addressee_id FOREIGN KEY (addressee_id) REFERENCES "user" (id)
);