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
