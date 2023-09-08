CREATE TABLE account(
                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                     country VARCHAR(2) not null,
                     customer_id bigint not null
);
