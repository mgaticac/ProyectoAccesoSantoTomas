DROP DATABASE IF EXISTS fpdb;
CREATE DATABASE fpdb;
USE fpdb;

CREATE TABLE institute( -- SELECT * FROM institute
    id INT AUTO_INCREMENT,
    name VARCHAR(100),
    UNIQUE(name),
    PRIMARY KEY (id)
);

CREATE TABLE user_type( -- SELECT * FROM user_type
    id INT AUTO_INCREMENT,
    name VARCHAR(100),
    priority INT,
    PRIMARY KEY (id),
    UNIQUE (name)
);


CREATE TABLE user ( -- SELECT * FROM user
    id INT AUTO_INCREMENT,
    fullname VARCHAR(100),
    rut VARCHAR(15),
    user_type_id_fk INT,
    finger_print BLOB,
    institute_fk INT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_type_id_fk) REFERENCES user_type(id),
    FOREIGN KEY (institute_fk) REFERENCES institute(id)
);

CREATE TABLE history ( -- SELECT * FROM history
    id INT AUTO_INCREMENT,
    user_id_fk INT,
    register_date DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id_fk) REFERENCES user(id)
);



INSERT INTO user_type VALUES(NULL,'student',100);
INSERT INTO user_type VALUES(NULL,'Teacher',85);
INSERT INTO user_type VALUES(NULL,'personnel',75);
INSERT INTO user_type VALUES(NULL,'provider',50);
INSERT INTO user_type VALUES(NULL,'admin',0);
INSERT INTO user_type VALUES(NULL,'technician',25);


INSERT INTO institute VALUES(NULL,'Santo Tomas Rancagua');
INSERT INTO institute VALUES(NULL,'Antofagasta');
INSERT INTO institute VALUES(NULL,'Arica');
INSERT INTO institute VALUES(NULL,'Chillán');
INSERT INTO institute VALUES(NULL,'Concepción');
INSERT INTO institute VALUES(NULL,'Copiapó');
INSERT INTO institute VALUES(NULL,'Curicó');
INSERT INTO institute VALUES(NULL,'Iquique');
INSERT INTO institute VALUES(NULL,'La Serena');
INSERT INTO institute VALUES(NULL,'Los Ángeles');
INSERT INTO institute VALUES(NULL,'Osorno');
INSERT INTO institute VALUES(NULL,'Ovalle');
INSERT INTO institute VALUES(NULL,'Puente Alto');
INSERT INTO institute VALUES(NULL,'Puerto Montt');
INSERT INTO institute VALUES(NULL,'Punta Arenas');
INSERT INTO institute VALUES(NULL,'Rancagua');
INSERT INTO institute VALUES(NULL,'San Joaquín');
INSERT INTO institute VALUES(NULL,'Santiago Centro y Estación Central');
INSERT INTO institute VALUES(NULL,'Santiago UST');
INSERT INTO institute VALUES(NULL,'Talca');
INSERT INTO institute VALUES(NULL,'Temuco');
INSERT INTO institute VALUES(NULL,'Valdivia');
INSERT INTO institute VALUES(NULL,'Viña del Mar');



/*
DROP PROCEDURE IF EXISTS clone_user;

DELIMITER //
CREATE PROCEDURE clone_user(max_inserts INT, user_id_to_clone INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT DEFAULT 0;
    DECLARE exist_user_id INT;

    -- User data
    DECLARE _id INT;
    DECLARE _fullname VARCHAR(100);
    DECLARE _rut VARCHAR(15);
    DECLARE _user_type_id_fk INT;
    DECLARE _finger_print BLOB;
    DECLARE _institute_fk INT;

    -- fill user data
    SET _fullname = (SELECT fullname FROM user WHERE id = user_id_to_clone);
    SET _rut = (SELECT rut FROM user WHERE id = user_id_to_clone);
    SET _user_type_id_fk = (SELECT user_type_id_fk FROM user WHERE id = user_id_to_clone);
    SET _finger_print = (SELECT finger_print FROM user WHERE id = user_id_to_clone);
    SET _institute_fk = (SELECT institute_fk FROM user WHERE id = user_id_to_clone);
    

    WHILE i < max_inserts DO
        INSERT INTO user VALUES(NULL,_fullname,_rut,_user_type_id_fk,_finger_print,_institute_fk);
        SET i = i + 1;
    END WHILE;

    SELECT CAST(i AS VARCHAR(4)) AS 't';


END; //
DELIMITER ;

CALL clone_user(20,1);

*/

-- SELECT * INTO OUTFILE 'C:/your-directory/your-filename.csv'
--         FIELDS TERMINATED BY ','
--         ENCLOSED BY '"'
--         ESCAPED BY '\\'
--         LINES TERMINATED BY '\n'
--         FROM tableName
-- 

-- SELECT *
-- FROM history
-- INNER JOIN user
-- ON history.user_id_fk = user.id
-- WHERE history.register_date > DATE_SUB(CURDATE(), INTERVAL 1 DAY);





