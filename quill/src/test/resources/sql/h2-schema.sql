CREATE TABLE IF NOT EXISTS Product(
    id int,
    description VARCHAR(255),
    sku int
);

CREATE TABLE IF NOT EXISTS Circle(
  radius int,
  cname VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Rectangle(
    id_rec  bigint auto_increment,
    length_rec int,
    width_rec int
);