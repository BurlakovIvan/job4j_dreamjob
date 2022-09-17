CREATE TABLE if not exists users (
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL,
  email varchar(255),
  password varchar(255),
  UNIQUE (email)
);