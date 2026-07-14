# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Las Bestias** is a Spring Boot 3.5.3 REST API backend for a music band website. It handles news, events, ticket sales, music (Spotify), videos (YouTube), user auth, and image storage (AWS S3).

## Commands

### Build & Run
```bash
# Build (Windows)
./gradlew.bat build

# Run (Windows)
./gradlew.bat bootRun

# Build JAR
./gradlew.bat bootJar

# Run tests
./gradlew.bat test

# Run a single test class
./gradlew.bat test --tests "com.backend.LasBestias.LasBestiasApplicationTests"
```

### Environment
The app loads a `.env` file at the project root (via `dotenv-java`). Create one before running locally with these keys:

```
DB_URL=jdbc:mysql://localhost:3306/lasbestias
DB_USER=
DB_PASS=
AWS_ACCESS_KEY=
AWS_SECRET_KEY=
AWS_REGION=
AWS_BUCKET_NAME=
YOUTUBE_API_KEY=
YOUTUBE_CHANNEL_ID=
SPOTIFY_CLIENT_ID=
SPOTIFY_CLIENT_SECRET=
SPOTIFY_ARTIST_ID=
MERCADOPAGO_ACCESS_TOKEN=
FRONTEND_URL=http://localhost:3000
BACKEND_URL=http://localhost:8080
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
APP_ADMIN_EMAIL=
APP_ADMIN_PASSWORD=
JWT_SECRET_KEY=
JWT_EXPIRATION_MS=86400000
```

## Architecture

### Layer Structure
Standard layered architecture: `controller → service (interface + impl) → repository → model`, with DTOs and MapStruct mappers separating layers.

- `model/` — JPA entities (`Evento`, `Entrada`, `Noticia`, `Image`, `Usuario`, `Rol`, `PagoProcesado`)
- `repository/` — Spring Data JPA repositories; `specification/` subpackage for dynamic filtering
- `service/` — interfaces + `impl/` implementations; `dto/request/` for input, `dto/response/` for output; `mapper/` for MapStruct mappers
- `controller/` — REST controllers
- `config/` — Spring configuration beans
- `payment/` — MercadoPago-specific controllers (`PaymentController`, `WebhookController`)
- `Security/` — `RateLimitService` (Caffeine-based IP rate limiting)
- `util/` — `JwtUtil`, `PaginationUtil`, `ImageUtil`, `S3Support`

### Key Integration Points

**Authentication**: Stateless JWT via `JwtFilter` (pre-filter on every request). Token carries email + role. `DataInitializer` seeds the admin user on startup from env vars.

**Authorization**: Two roles — `ADMIN` (full access) and authenticated users. Most public routes (noticias, videos, music, events GET) are open. Admin-only routes: `POST /api/entradas/validar`, `GET /api/entradas/**`, `/api/admin/**`.

**Payment flow (MercadoPago)**:
1. `POST /api/pagos/crear-preferencia` — creates a MercadoPago preference; encodes buyer data in `external_reference` as pipe-delimited string
2. `POST /api/pagos/webhook` — receives MercadoPago IPN, queries MP API, creates `Entrada` records with unique QR tokens, generates QR images + PDF ticket, emails buyer; uses `PagoProcesado` table for idempotency

**Image storage**: Images uploaded to AWS S3 via `ImageServiceImpl`/`S3Support`. The `Image` entity stores a reference; entities like `Evento` store `imageId` pointing to the `Image` record.

**Caching**: Caffeine cache named `"spotify-musica"` with 1-hour TTL, 10-entry max, used in `MusicaService` to avoid repeated Spotify API calls.

**Spotify**: Client credentials flow — `SpotifyAuthServiceImpl` fetches a fresh token each time (no refresh token persistence). Token is used by `MusicaServiceImpl` to fetch artist data.

**Pagination**: Controllers use `Pageable` from Spring Data; `PaginationUtil.setTotalCountPageHttpHeaders` sets total count in `X-Total-Count` response header.

### Tech Stack
- Java 17, Spring Boot 3.5.3, Gradle
- MySQL (JPA `ddl-auto=update`)
- AWS S3 (v1 SDK)
- Caffeine cache
- MapStruct for DTO mapping
- Lombok
- JWT (jjwt 0.11.5 + auth0 java-jwt 4.4.0 — both present, jjwt is primary)
- SpringDoc OpenAPI (Swagger UI at `/swagger-ui/**`)
- iTextPDF for ticket PDFs, ZXing for QR codes
- JavaMail + Thymeleaf for emails
- Rate limiting: in-memory Caffeine (3 requests/IP/minute) on payment preference endpoint