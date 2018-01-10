DROP TABLE IF EXISTS "user", empty, author CASCADE;

CREATE TABLE "user" (
  id         SERIAL PRIMARY KEY,
  name       VARCHAR   NOT NULL,
  full_name   VARCHAR   NOT NULL,
  created_at TIMESTAMP NOT NULL
);

INSERT INTO "user" (name, full_name, created_at) VALUES
  ('steve', 'Steve Jobs', '1955-2-24 09:09:09'),
  ('bill', 'Bill Gates', '1955-10-28 23:23:23');

CREATE TABLE empty (
  id      SERIAL PRIMARY KEY,
  title   VARCHAR NOT NULL,
  content VARCHAR NOT NULL
);

CREATE TABLE author (
  id   SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL
);

INSERT INTO author (name) VALUES ('Stephen King'), ('Umberto Eco')