-- Insert initial users with encrypted passwords
-- Password for all users: "password123"
-- Encrypted using BCrypt with strength 10

INSERT INTO users (name, username, password, role) VALUES
    ('Admin Beheerder', 'admin', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'BEHEER'),
    ('Garage Medewerker', 'medewerker', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'MEDEWERKER'),
    ('Auto Monteur', 'monteur', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'MONTEUR'),
    ('Henk de Beheerder', 'henk', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'BEHEER'),
    ('Piet de Medewerker', 'piet', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'MEDEWERKER'),
    ('Jan de Monteur', 'jan', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'MONTEUR');