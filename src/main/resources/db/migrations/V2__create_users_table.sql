CREATE table users

(
    user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256),
    phone_number VARCHAR(15),
    email VARCHAR(320)
)