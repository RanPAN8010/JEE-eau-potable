CREATE TABLE IF NOT EXISTS Commune (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS Analyse (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_prelevement DATE NOT NULL,
    parametre VARCHAR(100) NOT NULL,
    valeur DOUBLE NOT NULL,
    unite VARCHAR(20),
    conforme BOOLEAN NOT NULL,
    commune_id INT,
    FOREIGN KEY (commune_id) REFERENCES Commune(id)
);