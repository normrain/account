CREATE TABLE event_log(
                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                     event_type VARCHAR NOT NULL,
                     object_id uuid not null
);