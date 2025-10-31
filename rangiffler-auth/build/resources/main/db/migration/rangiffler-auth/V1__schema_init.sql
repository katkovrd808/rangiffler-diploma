CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'authority_enum') THEN
        CREATE TYPE authority_enum AS ENUM ('read', 'write');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS "user"
(
    id                      uuid PRIMARY KEY DEFAULT uuid_generate_v1(),
    username                varchar(50) UNIQUE NOT NULL,
    password                varchar(255)       NOT NULL,
    enabled                 boolean            NOT NULL,
    account_non_expired     boolean            NOT NULL,
    account_non_locked      boolean            NOT NULL,
    credentials_non_expired boolean            NOT NULL
);

CREATE TABLE IF NOT EXISTS "authority"
(
    id        uuid PRIMARY KEY DEFAULT uuid_generate_v1(),
    user_id   uuid NOT NULL REFERENCES "user" (id),
    authority authority_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS "oauth2_registered_client"
(
    id                            varchar(100)  NOT NULL PRIMARY KEY,
    client_id                     varchar(100)  NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200),
    client_secret_expires_at      timestamp,
    client_name                   varchar(200)  NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types     varchar(1000) NOT NULL,
    redirect_uris                 varchar(1000),
    post_logout_redirect_uris     varchar(1000),
    scopes                        varchar(1000) NOT NULL,
    client_settings               text NOT NULL,
    token_settings                text NOT NULL
);

CREATE TABLE IF NOT EXISTS "oauth2_authorization_consent"
(
    registered_client_id varchar(100) NOT NULL,
    principal_name       varchar(200) NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE IF NOT EXISTS "oauth2_authorization"
(
    id                            varchar(100) PRIMARY KEY,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000),
    attributes                    text,
    state                         varchar(500),
    authorization_code_value      text,
    authorization_code_issued_at  timestamp,
    authorization_code_expires_at timestamp,
    authorization_code_metadata   text,
    access_token_value            text,
    access_token_issued_at        timestamp,
    access_token_expires_at       timestamp,
    access_token_metadata         text,
    access_token_type             varchar(100),
    access_token_scopes           varchar(1000),
    oidc_id_token_value           text,
    oidc_id_token_issued_at       timestamp,
    oidc_id_token_expires_at      timestamp,
    oidc_id_token_metadata        text,
    refresh_token_value           text,
    refresh_token_issued_at       timestamp,
    refresh_token_expires_at      timestamp,
    refresh_token_metadata        text,
    user_code_value               text,
    user_code_issued_at           timestamp,
    user_code_expires_at          timestamp,
    user_code_metadata            text,
    device_code_value             text,
    device_code_issued_at         timestamp,
    device_code_expires_at        timestamp,
    device_code_metadata          text
);

CREATE TABLE IF NOT EXISTS "spring_session"
(
    primary_id CHAR(36) PRIMARY KEY,
    session_id CHAR(36) NOT NULL,
    creation_time BIGINT NOT NULL,
    last_access_time BIGINT NOT NULL,
    max_inactive_interval INT NOT NULL,
    expiry_time BIGINT NOT NULL,
    principal_name VARCHAR(100)
);

CREATE UNIQUE INDEX IF NOT EXISTS spring_session_ix1 ON spring_session (session_id);
CREATE INDEX IF NOT EXISTS spring_session_ix2 ON spring_session (expiry_time);
CREATE INDEX IF NOT EXISTS spring_session_ix3 ON spring_session (principal_name);

CREATE TABLE IF NOT EXISTS "spring_session_attributes"
(
    session_primary_id CHAR(36) NOT NULL,
    attribute_name VARCHAR(200) NOT NULL,
    attribute_bytes bytea NOT NULL,
    PRIMARY KEY (session_primary_id, attribute_name),
    CONSTRAINT spring_session_attributes_fk FOREIGN KEY (session_primary_id)
        REFERENCES "spring_session" (primary_id) ON DELETE CASCADE
);