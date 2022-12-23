DROP DATABASE IF EXISTS frontdb;
CREATE DATABASE frontdb;
CREATE USER IF NOT EXISTS 'gongu'@'%' IDENTIFIED BY 'asdf1234';
GRANT ALL PRIVILEGES ON frontdb.* TO 'gongu'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

USE frontdb;

CREATE TABLE User (
	nickname		VARCHAR(20) 	NOT NULL	PRIMARY KEY,
    password		VARCHAR(20)		NOT NULL,
    email       	VARCHAR(20)		NOT NULL
);

CREATE TABLE Room (
    roomId          INT             NOT NULL    AUTO_INCREMENT,
    roomName        VARCHAR(20)     NOT NULL,
    nickname        VARCHAR(20)     NOT NULL,
    createdAt       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(roomId),
    FOREIGN KEY(nickname) references User(nickname) on update cascade
);

CREATE TABLE Chat (
    chatId          INT             NOT NULL    AUTO_INCREMENT,
    roomId          INT             NOT NULL,
    nickname        VARCHAR(20)     NOT NULL,
    content         VARCHAR(200),
    imagePath       VARCHAR(300),    
    createdAt       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(chatId),
    FOREIGN KEY(roomId) references Room(roomId),
    FOREIGN KEY(nickname) references User(nickname) on update cascade
);

CREATE TABLE Member (
    roomId          INT             NOT NULL,
    nickname        VARCHAR(20)     NOT NULL,
    createdAt       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(roomId, nickname),
    FOREIGN KEY(roomId) references Room(roomId),
    FOREIGN KEY(nickname) references User(nickname) on update cascade
);

CREATE TABLE product (
    `id`            INT             NOT NULL    AUTO_INCREMENT,
    `name`          VARCHAR(100)    NOT NULL,
    `goal`          INT             NOT NULL,
    `ordered`       INT             NOT NULL    DEFAULT 1,
    `people`        INT             NOT NULL    DEFAULT 1,
    `price`         INT             NOT NULL    DEFAULT 0,
    `url`           VARCHAR(200)    NOT NULL,
    `portion`       INT             NOT NULL,
    `unit`          VARCHAR(45)     NOT NULL,
    `category`      INT             NOT NULL,
    `tags`          VARCHAR(100)    NULL,
    `ends`          DATE            NOT NULL,
    `createdat`     DATETIME        NOT NULL    DEFAULT current_timestamp,
    `createdby`     VARCHAR(100)    NOT NULL,
    `deleted`       BOOLEAN         NOT NULL    DEFAULT 0 COMMENT '0-false 1-true',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    INDEX `IDX_SELLER` (`createdby` ASC, `createdat` ASC) INVISIBLE)
COMMENT = '	';

CREATE TABLE image (
    `id`            INT             NOT NULL    AUTO_INCREMENT,
    `product_id`    INT             NOT NULL,
    `type`          INT             NOT NULL    DEFAULT 1   COMMENT '1-thumbnail\n2-제품 이미지',
    `path`          VARCHAR(150)    NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `IDX_SEARCH` (`product_id` ASC, `type` ASC) VISIBLE
);

CREATE TABLE contents (
    `id`            INT             NOT NULL    AUTO_INCREMENT,
    `product_id`    INT             NOT NULL,
    `content`       TEXT            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `product_id_UNIQUE` (`product_id` ASC) VISIBLE
);

CREATE TABLE enrolled (
    `id`            INT             NOT NULL    AUTO_INCREMENT,
    `product_id`    INT             NOT NULL,
    `nickname`      VARCHAR(100)    NOT NULL,
    `quantity`      INT             NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE device (
    `id`            INT             NOT NULL    AUTO_INCREMENT,
    `nickname`      VARCHAR(100)    NOT NULL,
    `endpoint`      VARCHAR(500)    NOT NULL,
    `auth`          VARCHAR(50)     NOT NULL,
    `p256dh`        VARCHAR(150)    NOT NULL,
    PRIMARY KEY (`id`)
);


INSERT INTO User VALUES('admin', 'asdf1234', 'admin@gmail.com');
INSERT INTO User VALUES('john', 'asdf1234', 'john@gmail.com');
INSERT INTO User VALUES('jake', 'asdf1234', 'jake@gmail.com');
INSERT INTO User VALUES('shin', 'asdf1234', 'shin@gmail.com');
INSERT INTO User VALUES('park', 'asdf1234', 'park@gmail.com');