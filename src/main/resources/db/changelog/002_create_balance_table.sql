CREATE TABLE balance(
                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                     account_id uuid not null,
                     balance decimal not null DEFAULT 0,
                     currency VARCHAR(3) not null
);