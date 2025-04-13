--liquibase formatted sql
--changeset junior:202504130029
--comment: remove 'order' column from cards table

ALTER TABLE CARDS DROP COLUMN `order`;

--rollback: ADD COLUMN `order` INT NOT NULL AFTER description