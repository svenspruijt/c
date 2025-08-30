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