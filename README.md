# Pokémon Rental API

REST API do zarządzania wypożyczalnią Pokémonów do walk.  
Aplikacja została napisana w **Java + Spring Boot** i obsługuje:

- zarządzanie Pokémonami
- zarządzanie trenerami
- wypożyczenia Pokémonów
- logikę biznesową związaną ze statusem Pokémona i wypożyczeń
- walidację danych
- obsługę wyjątków
- filtrowanie zasobów

## Technologie

Projekt wykorzystuje:

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- H2 Database
- Jakarta Validation
- Maven

## Struktura projektu

Projekt jest podzielony na warstwy:

- `controllers` — endpointy REST
- `service` — logika biznesowa
- `repository` — komunikacja z bazą danych
- `models` — encje JPA
- `dto` — obiekty do komunikacji z API
- `exceptions` — własne wyjątki i obsługa błędów
- `enums` — typy wyliczeniowe statusów

## Model domenowy

### Pokémon

Każdy Pokémon posiada:

- `id`
- `name`
- `type`
- `level`
- `hp`
- `status`

Status Pokémona może przyjmować wartości:

- `AVAILABLE`
- `RENTED`
- `INJURED`

### Trainer

Każdy trener posiada:

- `id`
- `firstName`
- `lastName`
- `email`

### Rental

Każde wypożyczenie posiada:

- `id`
- `pokemonId`
- `trainerId`
- `rentedAt`
- `returnedAt`
- `status`

Status wypożyczenia może przyjmować wartości:

- `ACTIVE`
- `RETURNED`
- `CANCELLED`

## Logika biznesowa

### Tworzenie wypożyczenia

Podczas tworzenia wypożyczenia:

- trener musi istnieć
- Pokémon musi istnieć
- Pokémon musi mieć status `AVAILABLE`
- trener może mieć maksymalnie 3 aktywne wypożyczenia

Po utworzeniu wypożyczenia:

- status wypożyczenia ustawiany jest na `ACTIVE`
- `rentedAt` ustawiany jest automatycznie
- status Pokémona zmienia się na `RENTED`

### Zwrot Pokémona

Podczas zwrotu:

- można zwrócić tylko aktywne wypożyczenie
- `returnedAt` ustawiany jest automatycznie
- status wypożyczenia zmienia się na `RETURNED`

Dodatkowo należy podać informację, czy Pokémon wrócił kontuzjowany:

- jeśli tak, status Pokémona zmienia się na `INJURED`
- jeśli nie, status Pokémona zmienia się na `AVAILABLE`

### Anulowanie wypożyczenia

- można anulować tylko aktywne wypożyczenie
- status wypożyczenia zmienia się na `CANCELLED`
- status Pokémona wraca na `AVAILABLE`

### Leczenie Pokémona

- można leczyć tylko Pokémona ze statusem `INJURED`
- po leczeniu status zmienia się na `AVAILABLE`

### Usuwanie danych

- nie można usunąć Pokémona z aktywnym wypożyczeniem
- nie można usunąć trenera z aktywnym wypożyczeniem

## Walidacja

W projekcie zastosowano walidację przy pomocy `jakarta.validation`.

### Pokémon

- `name` — wymagane, unikalne
- `type` — wymagane
- `level` — zakres `1-100`
- `hp` — większe od `0`

### Trainer

- `firstName` — wymagane
- `lastName` — wymagane
- `email` — wymagane, poprawny format, unikalne

## DTO

Projekt nie wystawia encji bezpośrednio w kontrolerach.  
Do komunikacji z API wykorzystywane są DTO, np.:

### Pokémon
- `PokemonRequestDto`
- `PokemonResponseDto`
- `PokemonFilter`

### Trainer
- `TrainerRequestDto`
- `TrainerResponseDto`

### Rental
- `CreateRentalRequestDto`
- `ReturnRentalRequestDto`
- `RentalResponseDto`

## Endpointy

## Pokémony

- `POST /api/pokemons`
- `GET /api/pokemons`
- `GET /api/pokemons/{id}`
- `PUT /api/pokemons/{id}`
- `DELETE /api/pokemons/{id}`
- `PATCH /api/pokemons/{id}/heal`

### Filtrowanie Pokémonów

Możliwe jest filtrowanie przez parametry query, np.:

- `GET /api/pokemons?status=AVAILABLE`
- `GET /api/pokemons?type=Fire`
- `GET /api/pokemons?minLevel=10&maxLevel=30`
- `GET /api/pokemons?minHp=50&maxHp=120`

## Trenerzy

- `POST /api/trainers`
- `GET /api/trainers`
- `GET /api/trainers/{id}`
- `PUT /api/trainers/{id}`
- `DELETE /api/trainers/{id}`

## Wypożyczenia

- `POST /api/rentals`
- `GET /api/rentals`
- `GET /api/rentals?status=ACTIVE`
- `GET /api/rentals/trainer/{trainerId}`
- `GET /api/rentals/pokemon/{pokemonId}`
- `PATCH /api/rentals/{id}/return`
- `PATCH /api/rentals/{id}/cancel`

## Przykładowe requesty

### Dodanie Pokémona

```json
{
  "name": "Pikachu",
  "type": "Electric",
  "level": 25,
  "hp": 100
}