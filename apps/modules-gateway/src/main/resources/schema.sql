CREATE TABLE IF NOT EXISTS module_registrations (
    id BIGSERIAL PRIMARY KEY,
    module_id VARCHAR(255) NOT NULL,
    module_type VARCHAR(255) NOT NULL,
    home_id VARCHAR(255) NOT NULL,
    base_url VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    registered_at TIMESTAMP NOT NULL,
    last_heartbeat TIMESTAMP,
    description TEXT,
    UNIQUE(module_id, home_id)
);

CREATE INDEX IF NOT EXISTS idx_module_registrations_home_id ON module_registrations(home_id);
CREATE INDEX IF NOT EXISTS idx_module_registrations_module_type ON module_registrations(module_type);
CREATE INDEX IF NOT EXISTS idx_module_registrations_status ON module_registrations(status);
