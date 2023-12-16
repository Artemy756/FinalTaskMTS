CREATE table books

(
    book_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    auditory_id BIGINT REFERENCES auditories(auditory_id),
    start_time TIMESTAMP,
    end_time TIMESTAMP
)