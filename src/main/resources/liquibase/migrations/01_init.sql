--liquibase formatted sql

--changeset magofrays:init
CREATE TABLE event_type (
                            id    INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                            event_name  VARCHAR(255) NOT NULL
);

INSERT INTO event_type (event_name)
VALUES
    ('submit'),
    ('open_task'),
    ('task_success'),
    ('task_wrong'),
    ('node_add'),
    ('node_remove'),
    ('node_color'),
    ('node_value'),
    ('edge_add'),
    ('edge_remove'),
    ('edge_color'),
    ('edge_value');

CREATE TABLE session (
                         id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id UUID NOT NULL,
                         task_id UUID NOT NULL,
                         created_at timestamp default current_timestamp
);

CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE INDEX ON session (user_id);

CREATE TABLE event (
                       id UUID NOT NULL DEFAULT gen_random_uuid(),
                       created_at      TIMESTAMPTZ NOT NULL default current_timestamp,
                       session_id      UUID NOT NULL,
                       event_type_id   INTEGER NOT NULL,
                       event_entity_id INTEGER,
                       event_value     VARCHAR(1024),

                       FOREIGN KEY (session_id) REFERENCES session (id),
                       FOREIGN KEY (event_type_id) REFERENCES event_type (id)
);

SELECT create_hypertable('event', 'created_at');

CREATE INDEX ON event (created_at DESC, session_id);

CREATE INDEX ON event (event_type_id, created_at DESC);

SELECT add_retention_policy('event', INTERVAL '2 year');

ALTER TABLE event SET (timescaledb.compress = true);