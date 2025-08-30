-- Insert initial users with encrypted passwords
-- Password for all users: "password123"
-- Encrypted using BCrypt with strength 10

INSERT INTO users (name, username, password, role) VALUES
    ('Admin Beheerder', 'admin', '$2a$10$uRpCJfNKRKlu5cTRYsbUwug/CcEyGl6PhirUyD2IBGaOGBZZ5EeL2', 'BEHEER'),
    ('Garage Medewerker', 'medewerker', '$2a$10$uRpCJfNKRKlu5cTRYsbUwug/CcEyGl6PhirUyD2IBGaOGBZZ5EeL2', 'MEDEWERKER'),
    ('Auto Monteur', 'monteur', '$2a$10$uRpCJfNKRKlu5cTRYsbUwug/CcEyGl6PhirUyD2IBGaOGBZZ5EeL2', 'MONTEUR'),
    ('Henk de Beheerder', 'henk', '$2a$10$uRpCJfNKRKlu5cTRYsbUwug/CcEyGl6PhirUyD2IBGaOGBZZ5EeL2', 'BEHEER'),
    ('Piet de Medewerker', 'piet', '$2a$10$uRpCJfNKRKlu5cTRYsbUwug/CcEyGl6PhirUyD2IBGaOGBZZ5EeL2', 'MEDEWERKER'),
    ('Jan de Monteur', 'jan', '$2a$10$uRpCJfNKRKlu5cTRYsbUwug/CcEyGl6PhirUyD2IBGaOGBZZ5EeL2', 'MONTEUR');

-- Insert sample customers
INSERT INTO customers (name, phonenumber) VALUES
    ('Jan Jansen', '+31612345678'),
    ('Maria van der Berg', '0687654321'),
    ('Peter de Vries', '+31-6-11223344'),
    ('Anna Bakker', '06 98765432'),
    ('Robert Smith', '+31 6 55667788'),
    ('Lisa de Jong', '0612398765'),
    ('Kees van Dijk', '+31-612-345-123'),
    ('Sandra Mulder', '06-87654321');

-- Insert sample cars
INSERT INTO cars (customer_id, brand, model, license_plate) VALUES
    (1, 'Volkswagen', 'Golf', '12-ABC-3'),
    (1, 'Toyota', 'Yaris', 'XY-123-Z'),
    (2, 'BMW', '3 Serie', 'AB-123-CD'),
    (3, 'Mercedes', 'C-Klasse', '99-XYZ-9'),
    (4, 'Audi', 'A4', 'ZZ-999-AA'),
    (4, 'Ford', 'Focus', '11-BBB-22'),
    (5, 'Opel', 'Corsa', 'CC-456-DD'),
    (6, 'Renault', 'Clio', 'EE-789-FF'),
    (7, 'Peugeot', '308', 'GG-321-HH'),
    (8, 'Skoda', 'Octavia', 'II-654-JJ'),
    (8, 'Hyundai', 'i30', 'KK-987-LL');