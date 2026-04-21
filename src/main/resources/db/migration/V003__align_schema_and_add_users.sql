CREATE TABLE IF NOT EXISTS expertise_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_expertise_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS specialist (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    level VARCHAR(255),
    fee_rate DOUBLE,
    profile_description VARCHAR(255),
    status VARCHAR(255),
    category_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_specialist_category FOREIGN KEY (category_id) REFERENCES expertise_category (id)
);

CREATE TABLE IF NOT EXISTS availability_slot (
    id BIGINT NOT NULL AUTO_INCREMENT,
    specialist_id BIGINT NOT NULL,
    slot_date DATE,
    start_time TIME,
    end_time TIME,
    booked BIT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_slot_specialist FOREIGN KEY (specialist_id) REFERENCES specialist (id)
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    topic VARCHAR(255),
    notes VARCHAR(255),
    rejection_reason VARCHAR(255),
    calculated_fee DOUBLE,
    created_at DATETIME(6),
    status VARCHAR(255),
    specialist_id BIGINT,
    slot_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_booking_specialist FOREIGN KEY (specialist_id) REFERENCES specialist (id),
    CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id) REFERENCES availability_slot (id)
);

CREATE TABLE IF NOT EXISTS booking_audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT,
    old_status VARCHAR(255),
    new_status VARCHAR(255),
    operator_username VARCHAR(255),
    remark VARCHAR(255),
    operated_at DATETIME(6),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT,
    specialist_id BIGINT,
    customer_email VARCHAR(255),
    customer_name VARCHAR(255),
    rating INTEGER,
    comment VARCHAR(255),
    created_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_feedback_booking FOREIGN KEY (booking_id) REFERENCES booking (id),
    CONSTRAINT fk_feedback_specialist FOREIGN KEY (specialist_id) REFERENCES specialist (id)
);

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    specialist_id BIGINT,
    reset_token VARCHAR(255),
    verification_token VARCHAR(255),
    email_verified BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_app_user_username UNIQUE (username),
    CONSTRAINT uk_app_user_email UNIQUE (email),
    CONSTRAINT fk_app_user_specialist FOREIGN KEY (specialist_id) REFERENCES specialist (id)
);

CREATE INDEX idx_slot_specialist_date ON availability_slot (specialist_id, slot_date, booked);
CREATE INDEX idx_booking_customer_email ON booking (customer_email);
CREATE INDEX idx_booking_specialist_status ON booking (specialist_id, status);
CREATE INDEX idx_audit_booking ON booking_audit_log (booking_id, operated_at);
CREATE INDEX idx_feedback_specialist ON feedback (specialist_id, created_at);
