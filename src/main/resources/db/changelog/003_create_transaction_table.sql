CREATE TABLE transaction(
                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                     account_id uuid not null,
                     amount decimal not null DEFAULT 0,
                     currency VARCHAR(3) not null,
                     direction VARCHAR not null,
                     description VARCHAR
);