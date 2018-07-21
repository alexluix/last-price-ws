# last-price-ws

[![Build Status](https://travis-ci.org/landpro/last-price-ws.svg?branch=master)](https://travis-ci.org/landpro/last-price-ws)
[![codecov](https://codecov.io/gh/landpro/last-price-ws/branch/master/graph/badge.svg)](https://codecov.io/gh/landpro/last-price-ws)

Price aggregator and last price repository web service.

## Functionality

-   receiving price data in batches
-   processing multiple batches concurrently
-   overwriting older prices with latest prices

## Non-functional requirements

-   application should cope with large batches of incoming data ~ 1MB

## Technologies

-   Java 8
-   Spring Boot
-   Jackson JSON
-   Maven

## Build

-   build: `mvn clean package`

## REST Endpoints

Description of API endpoints.

### Creating batch

POST /pricing/batch

### Posting price data to batch

POST /pricing/batch/{batch-id}

### Completing batch

POST /pricing/batch/{batch-id}/complete

### Cancelling batch

POST /pricing/batch/{batch-id}/cancel

### Request price

GET /pricing/instrument/{instrument-id}/price
