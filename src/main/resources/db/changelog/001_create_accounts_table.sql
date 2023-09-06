CREATE TABLE account(
                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                     country VARCHAR(2) not null,
                     customer_id uuid not null DEFAULT gen_random_uuid(),
                     currency VARCHAR(3)[] not null
);
