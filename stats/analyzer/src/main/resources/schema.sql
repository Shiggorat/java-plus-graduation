CREATE SCHEMA IF NOT EXISTS stats_analyzer;

DROP TABLE IF EXISTS stats_analyzer.user_action, stats_analyzer.event_similarity;

CREATE TABLE stats_analyzer.user_action (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE stats_analyzer.event_similarity (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_a BIGINT NOT NULL,
    event_b BIGINT NOT NULL,
    score DOUBLE PRECISION NOT NULL
);

CREATE UNIQUE INDEX idx_event_similarity_pair
ON stats_analyzer.event_similarity (event_a, event_b);

