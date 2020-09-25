DROP DATABASE IF EXISTS fpdatabase;
CREATE DATABASE fpdatabase;
USE fpdatabase;

CREATE TABLE user_type(
    id INT AUTO_INCREMENT,
    name VARCHAR(100),
    priority INT, -- 0 100
    PRIMARY KEY (id),
    UNIQUE (name)
);


CREATE TABLE user (
    id INT AUTO_INCREMENT,
    fullname VARCHAR(100),
    rut VARCHAR(15),
    user_type_id_fk INT,
    finger_print BLOB,
    PRIMARY KEY (id),
    FOREIGN KEY (user_type_id_fk) REFERENCES user_type(id)
);

CREATE TABLE history (
    id INT AUTO_INCREMENT,
    user_id_fk INT,
    register_date DATETIME, -- SOLO INGRESO
    temperature VARCHAR(10),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id_fk) REFERENCES user(id)
);

INSERT INTO history VALUES(NULL,1,NOW(),'35.4');
-- Query

--Orden comboBox Estudiante - Docente - Personal - Proveedor
--User type Inserts
INSERT INTO user_type VALUES(NULL,'student',100);
INSERT INTO user_type VALUES(NULL,'Teacher',85);
INSERT INTO user_type VALUES(NULL,'personnel',75);
INSERT INTO user_type VALUES(NULL,'provider',50);
INSERT INTO user_type VALUES(NULL,'admin',0);
INSERT INTO user_type VALUES(NULL,'technician',25);





-- Example user Insert
-- INSERT INTO user VALUES(NULL, 'Marcelo Gatica Contreras', '19.387.802-4','36.2',4,12345);

-- Example History Insert
-- INSERT INTO history VALUES(NULL,0,NOW);


-- SHOW ALL USER HISTORY BY NAME
SELECT user.fullname,history.register_date
FROM user
INNER JOIN history ON history.user_id_fk = user.id;

SELECT id FROM user WHERE rut LIKE 'm4';

SELECT * FROM history;
