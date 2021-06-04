CREATE TABLE IF NOT EXISTS Product(
    id int,
    description VARCHAR(255),
    sku int
);

CREATE TABLE IF NOT EXISTS Circlebasic(
     radius integer,
     cname VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Circle(
  id integer not null,
  radius integer,
  cname VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS CircleInfo(
    id INTEGER NOT NULL,
    info1 VARCHAR(255),
    info2 VARCHAR2(255),
    circle_id INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(circle_id) REFERENCES Circle(id)
);

CREATE TABLE IF NOT EXISTS Rectangle(
    id_rec  bigint auto_increment,
    length_rec int,
    width_rec int
);

