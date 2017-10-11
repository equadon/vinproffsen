CREATE TABLE IF NOT EXISTS PUBLIC.items
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    name2 VARCHAR(255),
    type VARCHAR(255),
    sell_start VARCHAR(255),
    area VARCHAR(255),
    country VARCHAR(255),
    producer VARCHAR(255),
    supplier VARCHAR(255),
    year INT,
    price INT NOT NULL,
    ecological BOOL DEFAULT FALSE,
    kosher BOOL DEFAULT FALSE,
    assortment VARCHAR(255),
    description VARCHAR(255),
    volume INT,
    price_per_liter INT,
    deposit INT,
    packaging VARCHAR(255),
    seal VARCHAR(255),
    alcohol DOUBLE,
    from_full VARCHAR(255),

    name_lower VARCHAR(255) AS LOWER(name),
    name2_lower VARCHAR(255) AS LOWER(name2),
    type_lower VARCHAR(255) AS LOWER(type),
    area_lower VARCHAR(255) AS LOWER(area),
    country_lower VARCHAR(255) AS LOWER(country),
    producer_lower VARCHAR(255) AS LOWER(producer),
    supplier_lower VARCHAR(255) AS LOWER(supplier)
);

CREATE INDEX IF NOT EXISTS name_lower_idx ON ITEMS(name_lower);
CREATE INDEX IF NOT EXISTS name2_lower_idx ON ITEMS(name2_lower);
CREATE INDEX IF NOT EXISTS type_lower_idx ON ITEMS(type_lower);
CREATE INDEX IF NOT EXISTS area_lower_idx ON ITEMS(area_lower);
CREATE INDEX IF NOT EXISTS country_lower_idx ON ITEMS(country_lower);
CREATE INDEX IF NOT EXISTS producer_lower_idx ON ITEMS(producer_lower);
CREATE INDEX IF NOT EXISTS supplier_lower_idx ON ITEMS(supplier_lower);