--liquibase formatted sql

--changeset magofrays:statistic
CREATE TABLE statistic (
                           id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
                           scope           VARCHAR(255) NOT NULL,
                           updated_at      TIMESTAMPTZ NOT NULL,
                           type            VARCHAR(255) NOT NULL,
                           value           FLOAT NOT NULL,
                           task_id         UUID,
                           user_id         UUID,
                           event_type      VARCHAR(255) NOT NULL
);