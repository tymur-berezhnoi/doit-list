DROP TABLE IF EXISTS TODO_ITEM;

CREATE TABLE IF NOT EXISTS TODO_ITEM
(
    ID          INTEGER PRIMARY KEY AUTO_INCREMENT,
    DESCRIPTION TEXT                 NOT NULL,
    CREATED_AT  TIMESTAMP            NOT NULL DEFAULT NOW(),
    STATUS      ENUM ('NEW', 'DONE') NOT NULL DEFAULT 'NEW'
);