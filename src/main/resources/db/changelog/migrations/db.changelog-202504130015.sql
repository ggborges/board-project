--liquibase formatted sql
--changeset junior:202504130015
--comment: corrigindo nome da coluna king para kind

ALTER TABLE BOARDS_COLUMNS CHANGE COLUMN king kind VARCHAR(7) NOT NULL;

--rollback ALTER TABLE BOARDS_COLUMNS CHANGE COLUMN kind king VARCHAR(7) NOT NULL;
