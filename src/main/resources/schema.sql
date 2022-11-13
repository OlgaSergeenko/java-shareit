DROP TABLE IF EXISTS SHAREIT_USER cascade;
DROP TABLE IF EXISTS ITEM cascade;
DROP TABLE IF EXISTS BOOKING cascade;
DROP TABLE IF EXISTS COMMENTS cascade;

CREATE TABLE IF NOT EXISTS SHAREIT_USER
(
    id    bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ITEM
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(250) NOT NULL,
    description  VARCHAR(250) NOT NULL,
    is_available BOOLEAN      NOT NULL,
    owner_id     BIGINT       NOT NULL REFERENCES SHAREIT_USER (id)
);

CREATE TABLE IF NOT EXISTS BOOKING
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL REFERENCES ITEM (id),
    booker_id  BIGINT                      NOT NULL REFERENCES SHAREIT_USER (id),
    status     VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    comment_text  VARCHAR                     NOT NULL,
    item_id       BIGINT                      NOT NULL REFERENCES ITEM (id),
    author_id     BIGINT                      NOT NULL REFERENCES SHAREIT_USER (id),
    creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);