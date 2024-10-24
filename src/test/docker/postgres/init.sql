-- Tables --

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    roles TEXT[] DEFAULT ARRAY['ROLE_USER']::TEXT[] NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_profiles (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    dni VARCHAR(50) NOT NULL UNIQUE,
    gender VARCHAR(10) NOT NULL,
    birth_date VARCHAR(50) NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_payment_profiles (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_payment_cards (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    payment_profile_id VARCHAR(255) NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    expiration_month INTEGER NOT NULL,
    expiration_year INTEGER NOT NULL,
    card_default BOOLEAN DEFAULT FALSE,
    payment_type VARCHAR(255) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    security_code_length INTEGER NOT NULL,
    security_code_card_location VARCHAR(255) NOT NULL,
    issuer_id VARCHAR(255) NOT NULL,
    issuer_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS spots (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    landlord_user_id VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    location POINT DEFAULT point(0, 0) NOT NULL,
    address JSONB DEFAULT '{}'::JSONB,
    capacity INT NOT NULL,
    price_per_minute DOUBLE PRECISION DEFAULT 15.0,
    description TEXT NOT NULL,
    state VARCHAR(50) NOT NULL,
    photos TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    client_user_id VARCHAR(255) NOT NULL,
    spot_owner_id VARCHAR(255) NOT NULL,
    spot_id VARCHAR(255) NOT NULL,
    payment_id VARCHAR(255),
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_time VARCHAR DEFAULT TO_CHAR(CURRENT_TIMESTAMP, 'HH24:MI:SS'),
    end_date TIMESTAMP,
    end_time VARCHAR(50),
    total_minutes INTEGER DEFAULT 0,
    state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    reference_id VARCHAR(255),
    method VARCHAR(50) NOT NULL,
    provider VARCHAR(50) DEFAULT 'NOT_DEFINED',
    total_price DOUBLE PRECISION DEFAULT 0.0,
    provider_amount DOUBLE PRECISION DEFAULT 0.0,
    platform_amount DOUBLE PRECISION DEFAULT 0.0,
    user_net_amount DOUBLE PRECISION DEFAULT 0.0,
    currency VARCHAR(3) DEFAULT 'ARS',
    last_status VARCHAR(50) DEFAULT 'PENDING',
    transaction_details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_balances (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id VARCHAR(255) NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    amount_pending_to_withdraw DOUBLE PRECISION NOT NULL,
    amount_available_to_withdraw DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

-- Initial Data --
INSERT INTO users(email, password, state, roles)
VALUES ('user@example.com', '$2a$10$w1tANE5zOBNJ8m5HxeaGBe5v22flIBkPPn.CcYuVEyOBToasqAGu.', 'ACTIVE', ARRAY['ROLE_USER']);

INSERT INTO users(email, password, state, roles)
VALUES ('user2@example.com', '$2a$10$kMQldozY8aK6iSodD0Tl1uQDoExQJjjd9NXc260oXGkxzs.2FAIkG', 'ACTIVE', ARRAY['ROLE_USER']);

INSERT INTO user_profiles (user_id, name, surname, dni, gender, birth_date, nationality)
VALUES ((SELECT id FROM users WHERE email = 'user@example.com'),'Juan', 'Pérez', '12345678', 'MALE', '15/10/1996', 'ARGENTINEAN');

INSERT INTO user_profiles (user_id ,name, surname, dni, gender, birth_date, nationality)
VALUES ((SELECT id FROM users WHERE email = 'user2@example.com'),'Camila', 'Boniato', '12345679', 'FEMALE', '15/10/1996', 'ARGENTINEAN');

INSERT INTO user_payment_profiles (user_id, customer_id)
VALUES ((SELECT id FROM users WHERE email = 'user2@example.com'), '2045962604-jef6hMCbCinmle');

INSERT INTO user_payment_profiles (user_id, customer_id)
VALUES ((SELECT id FROM users WHERE email = 'user@example.com'), '2044842671-qk2aKNIOhhyMEx');

INSERT INTO spots(landlord_user_id, type, location, address, capacity, price_per_minute, description, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user@example.com'),
    'GARAGE',
    point(-64.09208977582006, -31.464993937374985),
    '{
            "houseNumber": "6924",
            "displayName": "Caprichos, 6924, Avenida Vucetich, Ituzaingó, Córdoba, Municipio de Córdoba, Pedanía Capital, Departamento Capital, Córdoba, X5020, Argentina",
            "road": "Avenida Vucetich",
            "quarter": null,
            "suburb": "Ituzaingó",
            "cityDistrict": null,
            "city": "Córdoba",
            "stateDistrict": "Córdoba",
            "state": "Córdoba",
            "postcode": "X5020",
            "country": "Argentina",
            "license": "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright"
    }'::JSONB,
    5,
    15.0,
    'Beautiful spot',
    'AVAILABLE');

INSERT INTO payments(method, transaction_details)
VALUES ('CREDIT_CARD',
        jsonb_build_array(
                jsonb_build_object(
                    'date', CURRENT_TIMESTAMP,
                    'status', 'PENDING',
                    'statusDetail', 'Payment is pending'
                )
        ));

INSERT INTO bookings(client_user_id, spot_owner_id, spot_id, payment_id, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user2@example.com'),
    (SELECT id FROM users WHERE email = 'user@example.com'),
    (SELECT id FROM spots LIMIT 1),
    (SELECT id FROM payments LIMIT 1),
    'IN_PROGRESS');

INSERT INTO user_balances(user_id, total_amount, amount_pending_to_withdraw, amount_available_to_withdraw)
VALUES ((SELECT id FROM users WHERE email = 'user@example.com'), 500.0, 100.0, 400.0);


-- Indexes --
CREATE UNIQUE INDEX idx_user_id ON users(id);
CREATE UNIQUE INDEX idx_user_email ON users(email);
CREATE UNIQUE INDEX idx_user_profile ON user_profiles(id);
CREATE UNIQUE INDEX idx_user_payment_profile ON user_payment_profiles(id);
CREATE UNIQUE INDEX idx_user_payment_card ON user_payment_cards(id);
CREATE UNIQUE INDEX idx_spot_id ON spots(id);
CREATE UNIQUE INDEX idx_booking_id ON bookings(id);
CREATE UNIQUE INDEX idx_payment_id ON payments(id);
CREATE UNIQUE INDEX idx_user_balance_id ON user_balances(id);


-- Foreign Keys --
ALTER TABLE user_profiles
ADD CONSTRAINT fk_profile_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE NO ACTION;

ALTER TABLE user_payment_profiles
ADD CONSTRAINT fk_payment_profile_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE NO ACTION;

ALTER TABLE user_payment_cards
ADD CONSTRAINT fk_payment_card_payment_profile
FOREIGN KEY (payment_profile_id) REFERENCES user_payment_profiles(id)
ON DELETE NO ACTION;

ALTER TABLE spots
ADD CONSTRAINT fk_landlord_user
FOREIGN KEY (landlord_user_id) REFERENCES users(id)
ON DELETE NO ACTION;

ALTER TABLE bookings
ADD CONSTRAINT fk_client_user
FOREIGN KEY (client_user_id) REFERENCES users(id)
ON DELETE NO ACTION;

ALTER TABLE bookings
ADD CONSTRAINT fk_spot_owner
FOREIGN KEY (spot_owner_id) REFERENCES users(id)
ON DELETE NO ACTION;

ALTER TABLE bookings
ADD CONSTRAINT fk_spot
FOREIGN KEY (spot_id) REFERENCES spots(id)
ON DELETE NO ACTION;

ALTER TABLE bookings
ADD CONSTRAINT fk_payment
FOREIGN KEY (payment_id) REFERENCES payments(id)
ON DELETE NO ACTION;

ALTER TABLE user_balances
ADD CONSTRAINT fk_user_balance
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE NO ACTION;

COMMIT;