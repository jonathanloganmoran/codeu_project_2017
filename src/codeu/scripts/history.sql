
/**
 * This history will not be changed very much unless there is a attribute change
 * for example, add new tables, or add new columns; otherwise, this page remains
 * the same
 */

4.14
ALTER TABLE `CodeU_2017DB`.`User` 
ADD COLUMN `salt` VARCHAR(45) NOT NULL AFTER `password`;

4.15
ALTER TABLE `CodeU_2017DB`.`User`
CHANGE COLUMN `username` `username` VARCHAR(60) NOT NULL,
CHANGE COLUMN `Uuid` `Uuid` VARCHAR(60) NOT NULL ,
CHANGE COLUMN `password` `password` VARCHAR(64) NOT NULL ,
CHANGE COLUMN `salt` `salt` VARCHAR(60) NOT NULL;

4.20
ALTER TABLE Conversation
ADD FOREIGN KEY  id_user(Uuid)
REFERENCES User(Uuid)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE Message
ADD FOREIGN KEY user_id(Uuid)
REFERENCES User(Uuid)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE Message
ADD FOREIGN KEY id_conversation(Uuid)
REFERENCES Conversation(Uuid)
ON DELETE NO ACTION
ON UPDATE CASCADE;

4.20
USE CodeU_2017DB;

ALTER TABLE Message
ADD FOREIGN KEY id_conversation(id_conversation)
REFERENCES Conversation(Uuid)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE Message
ADD FOREIGN KEY user_id(user_id)
REFERENCES User(Uuid)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE Conversation
ADD FOREIGN KEY  id_user(id_user)
REFERENCES User(Uuid)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE `CodeU_2017DB`.`Message`
DROP FOREIGN KEY `Message_ibfk_2`,
DROP FOREIGN KEY `Message_ibfk_1`

ALTER TABLE `CodeU_2017DB`.`Conversation`
DROP FOREIGN KEY `Conversation_ibfk_1`;
