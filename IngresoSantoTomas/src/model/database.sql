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
    temperature VARCHAR(10),
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
INSERT INTO institute VALUES(NULL,'Santo Tomas Santiago');


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


