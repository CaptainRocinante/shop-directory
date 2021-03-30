CREATE TABLE bnpl_provider (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(512) NOT NULL,
  country_code              VARCHAR(10) NOT NULL,
  created_at                TIMESTAMP NOT NULL,
  updated_at                TIMESTAMP NOT NULL,
  UNIQUE (url, country_code)
);

CREATE TABLE merchant (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(512) NOT NULL,
  country_code              VARCHAR(10) NOT NULL,
  enabled                   BOOLEAN NOT NULL,
  bnpl_provider_uuid        UUID,
  created_at                TIMESTAMP NOT NULL,
  updated_at                TIMESTAMP NOT NULL,
  last_crawled_at           TIMESTAMP,
  UNIQUE (url, country_code),
  FOREIGN KEY (bnpl_provider_uuid) REFERENCES bnpl_provider (uuid)
)