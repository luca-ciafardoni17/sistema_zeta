CREATE TABLE  IF NOT EXISTS stored_files (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    file_title VARCHAR(255),
    file_description VARCHAR(500),
    file_name VARCHAR(255),
    file_extension VARCHAR(50),
    file_data BYTEA,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);