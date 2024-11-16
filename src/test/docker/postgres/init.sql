-- Schemas --
CREATE SCHEMA app;


-- Tables --
CREATE TABLE app.geo_zones (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    coordinates POINT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.users (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    roles TEXT[] DEFAULT ARRAY['ROLE_USER']::TEXT[] NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.user_vehicles (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id VARCHAR(255) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    patent VARCHAR(50) NOT NULL UNIQUE,
    year VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    size VARCHAR(50) NOT NULL,
    default_vehicle BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.user_profiles (
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
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.user_payment_profiles (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.user_payment_cards (
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
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.spots (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    landlord_user_id VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    location POINT DEFAULT point(0, 0),
    address JSONB DEFAULT '{}'::JSONB,
    zone VARCHAR(255),
    capacity INT NOT NULL,
    price_per_minute DOUBLE PRECISION DEFAULT 15.0,
    description TEXT NOT NULL,
    state VARCHAR(50) NOT NULL,
    photos TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.bookings (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    client_user_id VARCHAR(255) NOT NULL,
    spot_owner_id VARCHAR(255) NOT NULL,
    spot_id VARCHAR(255) NOT NULL,
    payment_id VARCHAR(255),
    vehicle_id VARCHAR(255),
    start_date TIMESTAMP,
    start_time VARCHAR(50),
    end_date TIMESTAMP,
    end_time VARCHAR(50),
    total_minutes INTEGER DEFAULT 0,
    state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.payments (
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
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.user_balances (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id VARCHAR(255) NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    amount_pending_to_withdraw DOUBLE PRECISION NOT NULL,
    amount_available_to_withdraw DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE
);

-- Initial Data --
INSERT INTO app.geo_zones (name, description, coordinates) VALUES
('ZONE_1', 'Zona Barrio Jardin, Cordoba',
    ARRAY[
        POINT(-64.18675009618985, -31.44663015073688),
        POINT(-64.18672056134612, -31.458396390926154),
        POINT(-64.16876337643856, -31.458371197129992),
        POINT(-64.16873384159484, -31.44663015073688),
        POINT(-64.18675009618985, -31.44663015073688)
    ]
),
('ZONE_2', 'Zona Barrio Nueva Cba, Cordoba',
    ARRAY[
        POINT(-64.18894963125472, -31.420328263491655),
        POINT(-64.1914970165038, -31.42651965453863),
        POINT(-64.1847418966925, -31.42861570684594),
        POINT(-64.18224000046595, -31.422016865224613),
        POINT(-64.18894963125472, -31.420328263491655)
    ]
),
('ZONE_3', 'Zona Barrio Parque Velez Sarsfield, Hospital Privado, Cordoba',
    ARRAY[
        POINT(-64.1987813959499, -31.443801637901345),
        POINT(-64.19727273204072, -31.440002230756114),
        POINT(-64.20416169133625, -31.43843589970512),
        POINT(-64.20396174792648, -31.439288855503293),
        POINT(-64.20445251811373, -31.441041267604923),
        POINT(-64.20563400189806, -31.441056775529816),
        POINT(-64.20599753537026, -31.44296423078547),
        POINT(-64.20405263129419, -31.443057276389382),
        POINT(-64.2038345112114, -31.444421934642953),
        POINT(-64.1987813959499, -31.443801637901345)
    ]
),
('ZONE_4', 'Zona Barrio Alberdi, Plaza de La Musica, Cordoba',
    ARRAY[
        POINT(-64.19949782864722, -31.40511204611176),
        POINT(-64.20078082692987, -31.408472269754327),
        POINT(-64.1913721728554, -31.411199008166925),
        POINT(-64.18997596884194, -31.407677850745024),
        POINT(-64.19949782864722, -31.40511204611176)
    ]
),
('ZONE_5', 'Zona Barrio Gral Paz',
    ARRAY[
        POINT(-64.17319103592125, -31.40874721635629),
        POINT(-64.1700596028737, -31.417190172004716),
        POINT(-64.1616027391007, -31.414773113134494),
        POINT(-64.16465439041501, -31.406551242506083),
        POINT(-64.17319103592125, -31.40874721635629)
    ]
),
('ZONE_6', 'Zona Barrio Alberdi, Club Atletico Belgrano, Cordoba',
    ARRAY[
        POINT(-64.20827132453326, -31.400638695196328),
        POINT(-64.21123047969203, -31.407647450956553),
        POINT(-64.20730959910676, -31.408831310725944),
        POINT(-64.20681421218652, -31.409241279512393),
        POINT(-64.20619733442652, -31.409475272401615),
        POINT(-64.20532913609827, -31.405887317305798),
        POINT(-64.2047579529865, -31.405068308323834),
        POINT(-64.20432385382198, -31.40378127976951),
        POINT(-64.20510066285375, -31.402747744061003),
        POINT(-64.20649434964466, -31.402260223267326),
        POINT(-64.2061516397783, -31.4012656729952),
        POINT(-64.20827132453326, -31.400638695196328)
    ]
),
('ZONE_7', 'Zona Barrio Parque Modelo, Cercanias Estadios Kempes, Cordoba',
    ARRAY[
        POINT(-64.25236787288895, -31.365809096389533),
        POINT(-64.25277781101674, -31.364332299942184),
        POINT(-64.25235670178866, -31.363081597258464),
        POINT(-64.24655271808524, -31.360267455378214),
        POINT(-64.25482842986445, -31.357828464274355),
        POINT(-64.25743089392562, -31.36433738477845),
        POINT(-64.25236787288895, -31.365809096389533)
    ]
),
('ZONE_8', 'Zona Barrio Cerro de las Rosas, Cordoba',
    ARRAY[
        POINT(-64.23425684633587, -31.367258691326654),
        POINT(-64.23782162938515, -31.371374140487397),
        POINT(-64.23616475839093, -31.372788784511663),
        POINT(-64.24038224819571, -31.37763270727214),
        POINT(-64.22597249136187, -31.38581965491158),
        POINT(-64.22326124791572, -31.382690696340063),
        POINT(-64.22361270539908, -31.38140479273445),
        POINT(-64.22290979043235, -31.38059037801228),
        POINT(-64.22306041506796, -31.37986168517076),
        POINT(-64.22185541798049, -31.37441774215884),
        POINT(-64.23330289030828, -31.366744247510212),
        POINT(-64.23425684633587, -31.367258691326654)
    ]
),
('ZONE_9', 'Zona Barrio Alta Cordoba, Estadio Juan Domingo Peron, Cordoba',
    ARRAY[
        POINT(-64.18655789906536, -31.38051952626875),
        POINT(-64.18653871057064, -31.38879194724572),
        POINT(-64.17746255262172, -31.388710045659195),
        POINT(-64.17748174111641, -31.388349677828344),
        POINT(-64.1771171597188, -31.387579796466248),
        POINT(-64.1772514791809, -31.38050314451369),
        POINT(-64.18655789906536, -31.38051952626875)
    ]
);

INSERT INTO public.users(email, password, state, roles)
VALUES ('user@example.com', '$2a$10$w1tANE5zOBNJ8m5HxeaGBe5v22flIBkPPn.CcYuVEyOBToasqAGu.', 'ACTIVE', ARRAY['ROLE_USER']);

INSERT INTO public.users(email, password, state, roles)
VALUES ('user2@example.com', '$2a$10$kMQldozY8aK6iSodD0Tl1uQDoExQJjjd9NXc260oXGkxzs.2FAIkG', 'ACTIVE', ARRAY['ROLE_USER']);

INSERT INTO public.users(email, password, state, roles)
VALUES ('user3@example.com', '$2a$10$kMQldozY8aK6iSodD0Tl1uQDoExQJjjd9NXc260oXGkxzs.2FAIkG', 'ACTIVE', ARRAY['ROLE_USER']);

INSERT INTO public.user_vehicles (user_id, brand, model, patent, year, type, size)
VALUES ((SELECT id FROM users WHERE email = 'user2@example.com'), 'Ford', 'KA', 'LGL308', '2012', 'CAR', 'SMALL');

INSERT INTO public.user_profiles (user_id, name, surname, dni, gender, birth_date, nationality)
VALUES ((SELECT id FROM users WHERE email = 'user@example.com'),'Juan', 'Pérez', '12345678', 'MALE', '15/10/1996', 'ARGENTINEAN');

INSERT INTO public.user_profiles (user_id ,name, surname, dni, gender, birth_date, nationality)
VALUES ((SELECT id FROM users WHERE email = 'user2@example.com'),'Camila', 'Boniato', '12345679', 'FEMALE', '15/10/1996', 'ARGENTINEAN');

INSERT INTO public.user_profiles (user_id ,name, surname, dni, gender, birth_date, nationality)
VALUES ((SELECT id FROM users WHERE email = 'user3@example.com'),'Roberto', 'Galatti', '12345680', 'MALE', '15/10/1996', 'ARGENTINEAN');

INSERT INTO public.user_payment_profiles (user_id, customer_id)
VALUES ((SELECT id FROM users WHERE email = 'user@example.com'), '2044842671-qk2aKNIOhhyMEx');

INSERT INTO public.user_payment_profiles (user_id, customer_id)
VALUES ((SELECT id FROM users WHERE email = 'user2@example.com'), '2045962604-jef6hMCbCinmle');

INSERT INTO public.user_payment_profiles (user_id, customer_id)
VALUES ((SELECT id FROM users WHERE email = 'user3@example.com'), '2045962604-jef6hMCbCinml4');

INSERT INTO public.spots(landlord_user_id, type, location, address, capacity, price_per_minute, description, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user@example.com'),
    'GARAGE',
    point(-64.18385103510106, -31.449264740397403),
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
    1,
    15.0,
    'Beautiful spot',
    'AVAILABLE');

INSERT INTO public.spots(landlord_user_id, type, location, address, capacity, price_per_minute, description, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user3@example.com'),
    'GARAGE',
    point(-64.17849201497079, -31.449021907575975),
    '{}'::JSONB,
    2,
    15.0,
    'Espacio amplio con capacidad para dos autos y almacenamiento adicional. Ubicado cerca del centro de la ciudad.',
    'AVAILABLE');

INSERT INTO public.spots(landlord_user_id, type, location, address, capacity, price_per_minute, description, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user3@example.com'),
    'GARAGE',
    point(-64.1736110459356, -31.451168601331055),
    '{}'::JSONB,
    1,
    15.0,
    'Cochera privada con acceso seguro, ideal para un vehículo y herramientas. Entrada con puerta automática.',
    'AVAILABLE');

INSERT INTO public.spots(landlord_user_id, type, location, address, capacity, price_per_minute, description, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user3@example.com'),
    'GARAGE',
    point(-64.18689330976461, -31.42640087268061),
    '{}'::JSONB,
    1,
    15.0,
    'Garage cubierto con espacio para un auto, con buena ventilación y fácil acceso desde la calle principal.',
    'AVAILABLE');

INSERT INTO public.payments(method, transaction_details)
VALUES ('CREDIT_CARD',
        jsonb_build_array(
                jsonb_build_object(
                    'date', CURRENT_TIMESTAMP,
                    'status', 'PENDING',
                    'statusDetail', 'Payment is pending'
                )
        ));

INSERT INTO public.bookings(client_user_id, spot_owner_id, spot_id, payment_id, vehicle_id, state)
VALUES (
    (SELECT id FROM users WHERE email = 'user2@example.com'),
    (SELECT id FROM users WHERE email = 'user@example.com'),
    (SELECT id FROM spots LIMIT 1),
    (SELECT id FROM payments LIMIT 1),
    (SELECT id FROM user_vehicles LIMIT 1),
    'PENDING');

INSERT INTO public.user_balances(user_id, total_amount, amount_pending_to_withdraw, amount_available_to_withdraw)
VALUES ((SELECT id FROM users WHERE email = 'user@example.com'), 500.0, 100.0, 400.0);


-- Indexes --
CREATE UNIQUE INDEX idx_user_id ON public.users(id);
CREATE UNIQUE INDEX idx_user_email ON public.users(email);
CREATE UNIQUE INDEX idx_user_profile ON public.user_profiles(id);
CREATE UNIQUE INDEX idx_user_payment_profile ON public.user_payment_profiles(id);
CREATE UNIQUE INDEX idx_user_payment_card ON public.user_payment_cards(id);
CREATE UNIQUE INDEX idx_spot_id ON public.spots(id);
CREATE UNIQUE INDEX idx_booking_id ON public.bookings(id);
CREATE UNIQUE INDEX idx_payment_id ON public.payments(id);
CREATE UNIQUE INDEX idx_user_balance_id ON public.user_balances(id);
CREATE UNIQUE INDEX idx_user_vehicle_id ON public.user_vehicles(id);


-- Foreign Keys --
ALTER TABLE public.user_profiles
ADD CONSTRAINT fk_profile_user
FOREIGN KEY (user_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

ALTER TABLE public.user_payment_profiles
ADD CONSTRAINT fk_payment_profile_user
FOREIGN KEY (user_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

ALTER TABLE public.user_payment_cards
ADD CONSTRAINT fk_payment_card_payment_profile
FOREIGN KEY (payment_profile_id) REFERENCES public.user_payment_profiles(id)
ON DELETE NO ACTION;

ALTER TABLE public.spots
ADD CONSTRAINT fk_landlord_user
FOREIGN KEY (landlord_user_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

ALTER TABLE public.bookings
ADD CONSTRAINT fk_client_user
FOREIGN KEY (client_user_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

ALTER TABLE public.bookings
ADD CONSTRAINT fk_spot_owner
FOREIGN KEY (spot_owner_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

ALTER TABLE public.bookings
ADD CONSTRAINT fk_spot
FOREIGN KEY (spot_id) REFERENCES public.spots(id)
ON DELETE NO ACTION;

ALTER TABLE public.bookings
ADD CONSTRAINT fk_payment
FOREIGN KEY (payment_id) REFERENCES public.payments(id)
ON DELETE NO ACTION;

ALTER TABLE public.user_balances
ADD CONSTRAINT fk_user_balance
FOREIGN KEY (user_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

ALTER TABLE public.user_vehicles
ADD CONSTRAINT fk_user_vehicle
FOREIGN KEY (user_id) REFERENCES public.users(id)
ON DELETE NO ACTION;

COMMIT;