CREATE TABLE entrepot (
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    city  VARCHAR(100) NOT NULL
);

CREATE TABLE stock_entrepot (
    id           BIGSERIAL PRIMARY KEY,
    produit_id   BIGINT  NOT NULL REFERENCES produit(id),
    entrepot_id  BIGINT  NOT NULL REFERENCES entrepot(id) ON DELETE CASCADE,
    quantity     INTEGER NOT NULL CHECK (quantity >= 0),
    CONSTRAINT uq_stock_produit_entrepot UNIQUE (produit_id, entrepot_id)
);

CREATE INDEX idx_stock_entrepot_entrepot ON stock_entrepot (entrepot_id);

INSERT INTO entrepot (name, city) VALUES
    ('Entrepôt Montréal', 'Montréal'),
    ('Entrepôt Laval', 'Laval'),
    ('Entrepôt Québec', 'Québec');

-- chaque produit existant est stocké dans chaque entrepôt, avec une quantité variable
INSERT INTO stock_entrepot (produit_id, entrepot_id, quantity)
SELECT p.id, e.id, 20 + ((p.id * 7 + e.id * 3) % 80)
FROM produit p CROSS JOIN entrepot e;