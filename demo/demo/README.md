# stock-api

# Stock API

API REST de gestion de stock (produits et catégories), construite avec Spring Boot, Spring Data JPA, PostgreSQL et Flyway.

## Prérequis

- Java 21
- Docker (pour PostgreSQL)

## Lancer la base de données

```bash
docker run --name stock-db -e POSTGRES_DB=stock -e POSTGRES_USER=stock -e POSTGRES_PASSWORD=stock -p 5433:5432 -d postgres:16
```

## Lancer l'application

```bash
./mvnw spring-boot:run
```

(Windows : `mvnw.cmd spring-boot:run`). Au démarrage, Flyway crée les tables et insère des données d'exemple. L'API est disponible sur http://localhost:8081.

## Documentation interactive

Swagger UI : http://localhost:8081/swagger-ui.html

## Endpoints

| Méthode | URL | Description |
|---|---|---|
| GET | `/api/categories` | Liste des catégories |
| GET | `/api/categories/{id}` | Détail d'une catégorie |
| POST | `/api/categories` | Créer une catégorie |
| PUT | `/api/categories/{id}` | Modifier une catégorie |
| DELETE | `/api/categories/{id}` | Supprimer une catégorie |

Exemple : `/api/produits?categorieId=5&prix=10&sort=prix,asc`

## Gestion des erreurs

Toutes les erreurs sont renvoyées au format `ProblemDetail` (RFC 9457) : 400 (validation), 404, 409 (categorie en double, contrainte de base), 500.

## Tests de l'API

Collection Postman dans le dossier `docs/`.