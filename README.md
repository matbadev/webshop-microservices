# Webshop built with Netflix Microservice Stack

This repository contains the sources of backend and frontend of a webshop created during the lecture *Distributed Systems* at [Karlsruhe University of Applied Sciences](https://www.hs-karlsruhe.de/). The authors are:

- Matthias Bäuerle ([matbadev](https://github.com/matbadev))
- Waldemar Felde ([YetiNerd](https://github.com/YetiNerd))
- Matthieu Laqua ([madjoel](https://github.com/madjoel))

## Project structure

The project is written in Java 11 based on Spring Boot using the Netflix Stack for microservices and [Docker Compose](https://docs.docker.com/compose/) for deploying each microservice as individual container. The project includes 12 microservices in total which are shown in the following figure with their relationships:

![webshop architecture](docs/webshop-architecture.png)

The 12 microservices are:

- **webclient**: webapp based on Struts 2 accessing the other microservices via the edge-service
- **edge-service**: API gateway using [Netflix Zuul](https://github.com/Netflix/zuul) as gateway service
- **authorization-service**: provides authorization using OAuth 2, users are stored in user-db
- **user-service** and **user-db**: core service providing access to users stored in a MySQL database
- **category-service** and **category-db**: core service providing access to categories stored in a MySQL database
- **product-service** and **product-db**: core service providing access to products stored in a MySQL database
- **inventory-service**: composite service combining access to products and categories, uses caching with [Netflix Hystrix](https://github.com/Netflix/Hystrix)
- **discovery-service**: microservice discovery service based on [Netflix Eureka](https://github.com/Netflix/eureka)
- **hystrix-dashboard-service**: webapp showing diagrams and statistics about Hystrix caching used in inventory-service

## Build & run

The project can be built and executed by using the project's [docker-compose.yml](docker-compose.yml) which uses Maven to compile the microservices.

- `docker-compose up -d edge-service`: Start the edge service including all the backend microservices except the Hystrix Dashboard. The command will not block the terminal as option `-d` (detached) is specified. This command also builds the corresponding Docker containers if they are not already built.
- `docker-compose up -d webclient`: Start the webapp. This does not automatically start the backend services.
- `docker-compose up -d --build edge-service`: (Re-)build the backend microservices if they have changed and start them.
- `docker-compose logs -f`: Show the logs of all running microservices. This command will block the terminal and show new logs once they are received as the option `-f` (follow) is specified.
- `docker-compose down`: Stop all running microservices.
