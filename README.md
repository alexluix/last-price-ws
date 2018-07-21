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

#### Request
`curl -X "POST" "http://localhost:8080/pricing/batch"`

#### Response
-   batch-id is returned:
```
HTTP/1.1 200

6
```

### Posting price data to batch

POST /pricing/batch/{batch-id}

#### Request
```
curl -X "POST" "http://localhost:8080/pricing/batch/6" \
     -H 'Content-Type: application/json' \
     -d $'{
  "prices": [
    {
      "refId": 766,
      "asOf": "2018-07-21T21:30:52.084",
      "payload": {
        "price": 1.28
      }
    },
    {
      "refId": 981,
      "asOf": "2018-07-21T21:30:59.001",
      "payload": {
        "price": 9.725
      }
    },
    {
      "refId": 480,
      "asOf": "2018-07-21T21:30:51.115",
      "payload": {
        "price": 1.125
      }
    }
  ]
}'
```

#### Response
-   When successfully posted:
`HTTP/1.1 200`
-   On processing error:
`HTTP/1.1 400`
-   When batch is not found:
`HTTP/1.1 404`

### Completing batch

POST /pricing/batch/{batch-id}/complete

#### Request
`curl -X "POST" "http://localhost:8080/pricing/batch/6/complete"`
#### Response
-   When successfully completed:
`HTTP/1.1 200`
-   When batch is not found:
`HTTP/1.1 404`

### Cancelling batch

POST /pricing/batch/{batch-id}/cancel

#### Request
`curl -X "POST" "http://localhost:8080/pricing/batch/6/cancel"`
#### Response
-   When successfully cancelled:
`HTTP/1.1 200`
-   When batch is not found:
`HTTP/1.1 404`

### Request price

GET /pricing/instrument/{instrument-id}/price

#### Request
`curl "http://localhost:8080/pricing/instrument/981/price"`
#### Response
-   When price found:
```
HTTP/1.1 200

{
  "refId": 981,
  "asOf": "2018-07-21T21:30:59.001",
  "payload": {
    "price": 9.725
  }
}
```
-   When batch is not found:
`HTTP/1.1 404`
