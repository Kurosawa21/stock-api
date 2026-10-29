-- ============================================================
-- V4__create_produit_table.sql
-- Création de la table produit avec sa relation vers categorie
-- ============================================================

CREATE TABLE produit (
    id           BIGSERIAL    PRIMARY KEY,
    sku          VARCHAR(20)  NOT NULL,
    categorie_id BIGINT       NOT NULL,

    -- Contrainte d'unicité sur le SKU
    CONSTRAINT uk_produit_sku UNIQUE (sku),

    -- Clé étrangère vers la table categorie
    CONSTRAINT fk_produit_categorie
        FOREIGN KEY (categorie_id)
        REFERENCES categorie (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Index sur la clé étrangère (bonne pratique : accélère les jointures)
CREATE INDEX idx_produit_categorie_id ON produit (categorie_id);

-- Commentaires (optionnel mais utile pour la doc)
COMMENT ON TABLE  produit              IS 'Table des produits en stock';
COMMENT ON COLUMN produit.sku          IS 'Référence unique du produit (Stock Keeping Unit)';
COMMENT ON COLUMN produit.categorie_id IS 'Référence vers la catégorie du produit';