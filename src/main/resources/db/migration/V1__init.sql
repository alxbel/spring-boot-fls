DROP TABLE IF EXISTS CONTACT;
DROP TABLE IF EXISTS APPLICATION;

CREATE TABLE CONTACT (
  CONTACT_ID serial PRIMARY KEY
);

CREATE TABLE APPLICATION (
  APPLICATION_ID serial PRIMARY KEY,
  DT_CREATED timestamp,
  PRODUCT_NAME varchar,
  CONTACT_ID_FK integer REFERENCES CONTACT(CONTACT_ID)
);

INSERT INTO CONTACT VALUES (1);
INSERT INTO CONTACT VALUES (2);
INSERT INTO CONTACT VALUES (3);

INSERT INTO APPLICATION(DT_CREATED, PRODUCT_NAME, CONTACT_ID_FK) VALUES('2018-07-21 00:00:00', 'product1', 1);
INSERT INTO APPLICATION(DT_CREATED, PRODUCT_NAME, CONTACT_ID_FK) VALUES('2018-07-22 00:00:00', 'product2', 1);
INSERT INTO APPLICATION(DT_CREATED, PRODUCT_NAME, CONTACT_ID_FK) VALUES('2018-07-21 00:00:00', 'product3', 2);
INSERT INTO APPLICATION(DT_CREATED, PRODUCT_NAME, CONTACT_ID_FK) VALUES('2018-07-22 00:00:00', 'product4', 2);
INSERT INTO APPLICATION(DT_CREATED, PRODUCT_NAME, CONTACT_ID_FK) VALUES('2018-07-23 00:00:00', 'product5', 3);
INSERT INTO APPLICATION(DT_CREATED, PRODUCT_NAME, CONTACT_ID_FK) VALUES('2018-07-24 00:00:00', 'product10', 3);