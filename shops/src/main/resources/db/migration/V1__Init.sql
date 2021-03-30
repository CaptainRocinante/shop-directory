DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS merchant_inferred_category;
DROP TABLE IF EXISTS merchant;
DROP TABLE IF EXISTS bnpl_provider;

CREATE TABLE bnpl_provider (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(2048) NOT NULL,
  created_at                TIMESTAMP NOT NULL,
  updated_at                TIMESTAMP NOT NULL,
  UNIQUE (url)
);

CREATE TABLE merchant (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(2048) NOT NULL,
  country_code              VARCHAR(10) NOT NULL,
  enabled                   BOOLEAN NOT NULL,
  bnpl_provider_uuid        UUID DEFAULT NULL,
  created_at                TIMESTAMP NOT NULL,
  updated_at                TIMESTAMP NOT NULL,
  last_crawled_at           TIMESTAMP DEFAULT NULL,
  UNIQUE (url, country_code),
  FOREIGN KEY (bnpl_provider_uuid) REFERENCES bnpl_provider (uuid)
);

CREATE TABLE merchant_inferred_category (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(2048) NOT NULL,
  enabled                   BOOLEAN NOT NULL,
  merchant_uuid             UUID NOT NULL,
  last_crawled_at           TIMESTAMP DEFAULT NULL,
  UNIQUE (url, merchant_uuid),
  FOREIGN KEY (merchant_uuid) REFERENCES merchant (uuid)
);

CREATE TABLE product (
  uuid                                          UUID PRIMARY KEY,
  name                      					          VARCHAR(128) NOT NULL,
  merchant_inferred_category_uuid               UUID NOT NULL,
  url                                           VARCHAR(2048) NOT NULL,
  enabled                                       BOOLEAN NOT NULL,
  currency_code                                 VARCHAR(10) NOT NULL,
  current_price_lower_range                     NUMERIC NOT NULL,
  current_price_upper_range                     NUMERIC NOT NULL,
  original_price_lower_range                    NUMERIC DEFAULT NULL,
  original_price_upper_range                    NUMERIC DEFAULT NULL,
  last_crawled_at                               TIMESTAMP DEFAULT NULL,
  UNIQUE (url),
  FOREIGN KEY (merchant_inferred_category_uuid) REFERENCES merchant_inferred_category (uuid)
);