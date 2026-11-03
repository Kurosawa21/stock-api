-- 1) Colonnes manquantes (IF NOT EXISTS : sans effet si la colonne existe déjà)
ALTER TABLE produit ADD COLUMN IF NOT EXISTS name        VARCHAR(100);
ALTER TABLE produit ADD COLUMN IF NOT EXISTS description VARCHAR(500);
ALTER TABLE produit ADD COLUMN IF NOT EXISTS price       NUMERIC(10,2);

-- 2) Valeurs pour les lignes déjà présentes (valeurs provisoires, à ta convenance)
UPDATE produit SET name  = sku                WHERE name  IS NULL;
UPDATE produit SET price = 5 + (id % 20)      WHERE price IS NULL;

-- 3) Contraintes une fois les lignes remplies
ALTER TABLE produit ALTER COLUMN name  SET NOT NULL;
ALTER TABLE produit ALTER COLUMN price SET NOT NULL;
ALTER TABLE produit ADD CONSTRAINT chk_produit_price_positive CHECK (price > 0);

-- 4) Source unique du stock : stock_entrepot (ligne sans effet si la colonne n'existe pas)
-- remplace "quantite" par le nom réel de ta colonne si elle s'appelle autrement
ALTER TABLE produit DROP COLUMN IF EXISTS quantite;