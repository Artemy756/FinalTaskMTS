CREATE table auditories

(
    auditory_id BIGSERIAL PRIMARY KEY,
    number VARCHAR(256),
    available_time TIME[]
)