CREATE TABLE if not exists users (
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL,
  UNIQUE (name)
);