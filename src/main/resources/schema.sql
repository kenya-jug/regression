CREATE TABLE IF NOT EXISTS users (
    uuid VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles_list_json TEXT,
    created_at TEXT
);

CREATE TABLE IF NOT EXISTS applications (
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    app_version VARCHAR(50),
    runtime_environment VARCHAR(100),
    owner_uuid VARCHAR(36),
    created_at TEXT,

    CONSTRAINT fk_app_owner FOREIGN KEY (owner_uuid) REFERENCES users(uuid)
);

CREATE TABLE IF NOT EXISTS app_logs (
    uuid VARCHAR(36) PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    severity VARCHAR(20),
    application_uuid VARCHAR(36),
    log_source VARCHAR(100),
    message TEXT,

    CONSTRAINT fk_log_application FOREIGN KEY (application_uuid) REFERENCES applications(uuid)
);

CREATE TABLE IF NOT EXISTS logs_metadata (
    uuid VARCHAR(36) PRIMARY KEY,
    log_uuid VARCHAR(36) NOT NULL,
    metadata_type VARCHAR(100) NOT NULL,
    metadata_value TEXT,

    CONSTRAINT fk_logs_metadata_log FOREIGN KEY (log_uuid) REFERENCES app_logs(uuid)
);
