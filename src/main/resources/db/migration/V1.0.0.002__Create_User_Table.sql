
CREATE TABLE oauth2_user (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(68) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    UNIQUE(email)
);

GRANT SELECT, INSERT, UPDATE (first_name, last_name, password_hash), DELETE ON oauth2_user TO oauth2user;
GRANT USAGE, SELECT ON SEQUENCE oauth2_user_user_id_seq TO oauth2user;

CREATE TABLE user_authority (
    user_id BIGINT NOT NULL REFERENCES oauth2_user (user_id) ON DELETE CASCADE,
    authority varchar(255) NOT NULL,
    UNIQUE(user_id, authority)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON user_authority TO oauth2user;