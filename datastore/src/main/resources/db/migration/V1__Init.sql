DROP TABLE IF EXISTS product_inferred_category_mapping;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS merchant_inferred_category;
DROP TABLE IF EXISTS bnpl_provider_merchant_mapping;
DROP TABLE IF EXISTS merchant;
DROP TABLE IF EXISTS bnpl_provider;

CREATE TABLE bnpl_provider (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(2048) NOT NULL,
  created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  UNIQUE (url)
);

CREATE TABLE merchant (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(2048) NOT NULL,
  country_code              VARCHAR(10) NOT NULL,
  enabled                   BOOLEAN NOT NULL,
  created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  last_crawled_at           TIMESTAMP WITH TIME ZONE DEFAULT NULL,
  UNIQUE (url, country_code)
);

CREATE TABLE bnpl_provider_merchant_mapping (
  bnpl_provider_uuid        UUID NOT NULL,
  merchant_uuid             UUID NOT NULL,
  PRIMARY KEY (bnpl_provider_uuid, merchant_uuid),
  FOREIGN KEY (bnpl_provider_uuid) REFERENCES bnpl_provider (uuid),
  FOREIGN KEY (merchant_uuid) REFERENCES merchant (uuid)
);

CREATE TABLE merchant_inferred_category (
  uuid                      UUID PRIMARY KEY,
  name                      VARCHAR(128) NOT NULL,
  url                       VARCHAR(2048) NOT NULL,
  enabled                   BOOLEAN NOT NULL,
  merchant_uuid             UUID NOT NULL,
  created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  last_crawled_at           TIMESTAMP WITH TIME ZONE DEFAULT NULL,
  UNIQUE (url, merchant_uuid),
  FOREIGN KEY (merchant_uuid) REFERENCES merchant (uuid)
);

CREATE TABLE product (
  uuid                                          UUID PRIMARY KEY,
  name                      					          VARCHAR(128) NOT NULL,
  url                                           VARCHAR(2048) NOT NULL,
  enabled                                       BOOLEAN NOT NULL,
  currency_code                                 VARCHAR(10) NOT NULL,
  current_price_lower_range                     NUMERIC NOT NULL,
  current_price_upper_range                     NUMERIC NOT NULL,
  original_price_lower_range                    NUMERIC DEFAULT NULL,
  original_price_upper_range                    NUMERIC DEFAULT NULL,
  main_image_url                                VARCHAR(2048) NOT NULL,
  created_at                                    TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at                                    TIMESTAMP WITH TIME ZONE NOT NULL,
  last_crawled_at                               TIMESTAMP WITH TIME ZONE DEFAULT NULL,
  UNIQUE (url)
);

CREATE TABLE product_inferred_category_mapping (
  product_uuid                                UUID NOT NULL,
  merchant_inferred_category_uuid             UUID NOT NULL,
  PRIMARY KEY (product_uuid, merchant_inferred_category_uuid),
  FOREIGN KEY (product_uuid) REFERENCES product (uuid),
  FOREIGN KEY (merchant_inferred_category_uuid) REFERENCES merchant_inferred_category (uuid)
);