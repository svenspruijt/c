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

-- Insert sample parts
INSERT INTO parts (name, price, stock) VALUES
    ('Remblokken set', 45.99, 25),
    ('Motorolie 5W-30', 35.50, 40),
    ('Luchtfilter', 12.75, 30),
    ('Bougies set', 28.90, 15),
    ('Ruitenwissers', 22.50, 18),
    ('Accu 12V', 89.99, 8),
    ('Koppeling set', 285.00, 5),
    ('Remschijven vooras', 125.50, 12),
    ('Distributieriem', 65.75, 10),
    ('Radiateur', 195.00, 6),
    ('Uitlaat demper', 78.25, 14),
    ('Brandstoffilter', 18.90, 22),
    ('Koelvloeistof', 24.50, 35),
    ('Schokbrekers set', 156.75, 8),
    ('Startmotor', 245.00, 4),
    ('Dynamo', 189.90, 3),
    ('Koplampen set', 145.50, 7),
    ('Remklauw', 98.75, 9),
    ('Veerpoten', 325.00, 6),
    ('Katalysator', 455.00, 2);

-- Insert sample actions
INSERT INTO actions (name, description, price) VALUES
    ('APK Keuring', 'Algemene Periodieke Keuring conform RDW eisen', 50.00),
    ('Kleine beurt', 'Olie verversen, filters controleren, vloeistoffen bijvullen', 125.00),
    ('Grote beurt', 'Uitgebreide onderhoudsbeurt met alle vloeistoffen en filters', 285.00),
    ('Remmen vervangen', 'Vervangen van remblokken en/of remschijven inclusief montage', 195.00),
    ('Banden wisselen', 'Seizoensbanden wisselen en balanceren', 45.00),
    ('Airco service', 'Airconditioning reinigen en koelmiddel bijvullen', 89.00),
    ('Uitlijning wielen', 'Uitlijning voorwielen en bandenprofiel controleren', 75.00),
    ('Accu vervangen', 'Oude accu vervangen door nieuwe inclusief installatie', 125.00),
    ('Reparatie uitlaat', 'Reparatie of vervanging van uitlaatdemper/pijp', 145.00),
    ('Motor diagnose', 'Computerscan en diagnose van motorproblemen', 85.00),
    ('Koppeling vervangen', 'Volledige koppeling vervangen inclusief druklager', 425.00),
    ('Distributieriem', 'Vervangen van distributieriem en spanrollen', 295.00),
    ('Verlichting reparatie', 'Reparatie of vervanging van voor/achterlichten', 65.00),
    ('Koelsysteem spoelen', 'Koelsysteem doorspoelen en nieuwe koelvloeistof', 95.00),
    ('Startmotor reparatie', 'Reparatie of vervanging van startmotor', 185.00);