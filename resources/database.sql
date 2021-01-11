DROP DATABASE raspisurveillance;

CREATE DATABASE IF NOT EXISTS raspisurveillance;
USE raspisurveillance;

DELIMITER //

CREATE FUNCTION UuidToBin(_uuid BINARY(36))
    RETURNS BINARY(16)
    LANGUAGE SQL  DETERMINISTIC  CONTAINS SQL  SQL SECURITY INVOKER
RETURN
    UNHEX(CONCAT(
        SUBSTR(_uuid, 15, 4),
        SUBSTR(_uuid, 10, 4),
        SUBSTR(_uuid,  1, 8),
        SUBSTR(_uuid, 20, 4),
        SUBSTR(_uuid, 25) ));
//
CREATE FUNCTION UuidFromBin(_bin BINARY(16))
    RETURNS BINARY(36)
    LANGUAGE SQL  DETERMINISTIC  CONTAINS SQL  SQL SECURITY INVOKER
RETURN
    LCASE(CONCAT_WS('-',
        HEX(SUBSTR(_bin,  5, 4)),
        HEX(SUBSTR(_bin,  3, 2)),
        HEX(SUBSTR(_bin,  1, 2)),
        HEX(SUBSTR(_bin,  9, 2)),
        HEX(SUBSTR(_bin, 11))
             ));

//
DELIMITER ;

CREATE TABLE IF NOT EXISTS roles (
	NR_ID BINARY(16) NOT NULL,
	STR_NAME VARCHAR(20),
	CONSTRAINT pk_roles
		PRIMARY KEY ( NR_ID )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS users (
	NR_ID BINARY(16) NOT NULL,
	STR_USERNAME VARCHAR(100),
	STR_EMAIL VARCHAR(100),
	STR_PASSWORD VARCHAR(100),
	STATUS_VERIFIED TINYINT(1) NOT NULL DEFAULT 0,
	JSON_DATA JSON CHECK (JSON_VALID(json_data)),
	CONSTRAINT pk_users
		PRIMARY KEY ( NR_ID ),
	UNIQUE KEY uk_username ( STR_USERNAME ),
	UNIQUE KEY uk_email ( STR_EMAIL )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS user_roles (
	NR_USER_ID BINARY(16) NOT NULL,
	NR_ROLE_ID BINARY(16) NOT NULL,
	CONSTRAINT pk_user_roles_id
		PRIMARY KEY ( NR_USER_ID, NR_ROLE_ID )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS user_forgot_password_tokens (
	NR_ID BINARY(16) NOT NULL,
	NR_USER_ID BINARY(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS user_verification_tokens (
	NR_ID BINARY(16) NOT NULL,
	NR_USER_ID BINARY(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS servers (
	NR_ID BINARY(16) NOT NULL,
	DATE_CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	STR_NAME VARCHAR(100),
	STR_URL VARCHAR(100),
	STR_USERNAME VARCHAR(100),
	STR_PASSWORD VARCHAR(100),
	IS_MASTER TINYINT(1) NOT NULL DEFAULT 0,
	HAS_SERVICE_CAMERASTREAM TINYINT(1) NOT NULL DEFAULT 0,
	HAS_SERVICE_SURVEILLANCE TINYINT(1) NOT NULL DEFAULT 0,
	STR_URL_MASTER VARCHAR(100),
	STR_ID_MASTER VARCHAR(100),
	STR_USERNAME_MASTER VARCHAR(100),
	STR_PASSWORD_MASTER VARCHAR(100),
	STR_URL_CAMERASTREAM VARCHAR(100),
	STR_USERNAME_CAMERASTREAM VARCHAR(100),
	STR_PASSWORD_CAMERASTREAM VARCHAR(100),
	STR_STATUS VARCHAR(20),
	JSON_DATA JSON CHECK (JSON_VALID(json_data)),
	CONSTRAINT pk_servers
		PRIMARY KEY ( NR_ID )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS attributes_camerastream (
	NR_ID VARCHAR(36) NOT NULL DEFAULT UUID(),
	NR_SERVER_ID BINARY(16) NOT NULL,
	STR_KEY VARCHAR(30) NOT NULL,
	STR_VALUE VARCHAR(30) NOT NULL,
	CONSTRAINT pk_attributes_camerastream_id
		PRIMARY KEY ( NR_ID, NR_SERVER_ID )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS attributes_surveillance (
	NR_ID VARCHAR(36) NOT NULL DEFAULT UUID(),
	NR_SERVER_ID BINARY(16) NOT NULL,
	STR_KEY VARCHAR(30) NOT NULL,
	STR_VALUE VARCHAR(30) NOT NULL,
	CONSTRAINT pk_attributes_surveillance_id
		PRIMARY KEY ( NR_ID, NR_SERVER_ID )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
