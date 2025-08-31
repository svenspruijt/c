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

-- Insert sample inspections
INSERT INTO inspections (car_id, date, report, status, is_paid) VALUES
    (1, '2024-01-15', 'APK keuring uitgevoerd. Auto voldoet aan alle eisen. Geen gebreken geconstateerd.', 'COMPLETED', true),
    (2, '2024-01-22', 'APK keuring uitgevoerd. Kleine gebreken aan ruitenwissers en verlichting geconstateerd.', 'COMPLETED', true),
    (3, '2024-02-05', 'APK keuring uitgevoerd. Ernstige gebreken aan remmen geconstateerd. Auto afgekeurd.', 'COMPLETED', false),
    (4, '2024-02-12', 'APK keuring in uitvoering. Voorlopige bevindingen: mogelijke problemen met uitlaat.', 'IN_PROGRESS', false),
    (5, '2024-02-18', 'APK keuring voltooid. Auto goedgekeurd met kleine aanbevelingen voor onderhoud.', 'COMPLETED', true),
    (6, '2024-02-25', 'APK keuring uitgevoerd. Banden onder minimum profiel, vervangen vereist.', 'COMPLETED', false),
    (7, '2024-03-01', 'APK keuring gepland voor vandaag. Wachten op beschikbaarheid monteur.', 'SCHEDULED', false),
    (8, '2024-03-05', 'APK keuring voltooid. Geen gebreken. Auto in uitstekende staat.', 'COMPLETED', true),
    (9, '2024-03-10', 'APK keuring uitgevoerd. Vervangen van koplampen vereist voor goedkeuring.', 'COMPLETED', false),
    (10, '2024-03-15', 'APK keuring in voorbereiding. Auto wordt voorbereid voor keuring.', 'SCHEDULED', false);

-- Insert sample repairs
INSERT INTO repairs (car_id, date, status, report, is_paid) VALUES
    (3, '2024-02-06', 'COMPLETED', 'Remblokken en remschijven vervangen. Auto weer veilig voor de weg.', true),
    (2, '2024-01-23', 'COMPLETED', 'Ruitenwissers vervangen en verlichting gerepareerd na APK keuring.', true),
    (6, '2024-02-26', 'IN_PROGRESS', 'Nieuwe banden besteld. Montage gepland voor morgen.', false),
    (9, '2024-03-11', 'SCHEDULED', 'Koplampen besteld voor vervanging na APK keuring.', false),
    (1, '2024-01-20', 'COMPLETED', 'Grote onderhoudsbeurt uitgevoerd. Alle vloeistoffen vervangen.', true),
    (5, '2024-02-20', 'COMPLETED', 'Kleine onderhoudsbeurt en airco service uitgevoerd.', false),
    (7, '2024-03-02', 'IN_PROGRESS', 'Diagnose motorproblemen lopende. Mogelijke koppeling problemen.', false),
    (4, '2024-02-15', 'COMPLETED', 'Uitlaat demper vervangen na APK keuring. Probleem opgelost.', true),
    (8, '2024-03-06', 'SCHEDULED', 'Distributieriem vervanging gepland voor volgende week.', false),
    (10, '2024-03-12', 'COMPLETED', 'Accu vervangen en motor diagnose uitgevoerd.', false);

-- Insert repair actions (linking repairs to standard actions)
INSERT INTO repair_actions (repair_id, action_id, amount) VALUES
    -- Repair 1 (car 3): Remmen vervangen
    (1, 4, 1),  -- Remmen vervangen
    -- Repair 2 (car 2): Ruitenwissers en verlichting
    (2, 13, 1), -- Verlichting reparatie
    -- Repair 4 (car 9): Koplampen
    (4, 13, 1), -- Verlichting reparatie
    -- Repair 5 (car 1): Grote beurt
    (5, 3, 1),  -- Grote beurt
    -- Repair 6 (car 5): Kleine beurt en airco
    (6, 2, 1),  -- Kleine beurt
    (6, 6, 1),  -- Airco service
    -- Repair 7 (car 7): Motor diagnose
    (7, 10, 1), -- Motor diagnose
    -- Repair 8 (car 4): Uitlaat reparatie
    (8, 9, 1),  -- Reparatie uitlaat
    -- Repair 9 (car 8): Distributieriem
    (9, 12, 1), -- Distributieriem
    -- Repair 10 (car 10): Accu en diagnose
    (10, 8, 1), -- Accu vervangen
    (10, 10, 1); -- Motor diagnose

-- Insert repair parts (linking repairs to parts used)
INSERT INTO repair_parts (repair_id, part_id, amount) VALUES
    -- Repair 1 (car 3): Remmen vervangen - remblokken en remschijven
    (1, 1, 1),  -- Remblokken set
    (1, 8, 1),  -- Remschijven vooras
    -- Repair 2 (car 2): Ruitenwissers vervangen
    (2, 5, 1),  -- Ruitenwissers
    -- Repair 4 (car 9): Koplampen vervangen
    (4, 17, 1), -- Koplampen set
    -- Repair 5 (car 1): Grote beurt - olie, filters
    (5, 2, 1),  -- Motorolie 5W-30
    (5, 3, 1),  -- Luchtfilter
    (5, 12, 1), -- Brandstoffilter
    (5, 13, 1), -- Koelvloeistof
    -- Repair 6 (car 5): Kleine beurt - alleen olie
    (6, 2, 1),  -- Motorolie 5W-30
    -- Repair 8 (car 4): Uitlaat demper vervangen
    (8, 11, 1), -- Uitlaat demper
    -- Repair 9 (car 8): Distributieriem
    (9, 9, 1),  -- Distributieriem
    -- Repair 10 (car 10): Accu vervangen
    (10, 6, 1); -- Accu 12V

-- Insert custom repair actions (specific work not covered by standard actions)
INSERT INTO repair_custom_actions (repair_id, description, price) VALUES
    (1, 'Extra controle wielophanging na remmen reparatie', 25.00),
    (2, 'Reiniging koplampen voor betere lichtopbrengst', 15.00),
    (3, 'Speciale banden uitlijning na vervangen', 35.00),
    (5, 'Controle en bijstellen carburateur', 45.00),
    (6, 'Reiniging luchtfilter behuizing', 20.00),
    (7, 'Uitgebreide computerdiagnose transmissie', 65.00),
    (8, 'Controle en reiniging katalysator', 55.00),
    (9, 'Controle waterpomp tijdens distributieriem vervanging', 40.00),
    (10, 'Controle en testen elektrische bedrading', 30.00);