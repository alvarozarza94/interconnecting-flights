# Interconnecting-flights
#### Deploy project with docker (On the root of the project "/interconnecting-flights"):

`$ docker-compose up`

#### Run unitary tests

`$ ./gradlew test`

The coverage of the unit tests is 100% over the code.


**Interesting endpoints:**


`Swagger Interface` : <http://localhost:8080/swagger-ui.html#>

`Endpoint` : http://localhost:8080/api/interconnections

`Endpoint example` : http://localhost:8080/api/interconnections?departure=MAD&arrival=DUB&departureDateTime=2021-01-29T17%3A00&arrivalDateTime=2021-03-03T21%3A00

**Considerations:**

Spring Boot 2, Java11, and Swagger have been used for this project.

The code has been organized with a controller to receive the requests and a service to do the business logic. The requests to other microservices are in service facades. The main idea to have all these services is to apply the Single-responsibility principle and decouple the code.

The endpoint search all the direct flights and interconnected flights between the airports and the selected dates. The flights dates are not earlier than the specified departure DateTime and arriving to a given arrival airport not later than the specified arrival DateTime.

Most of the business logic in the InterconnectionService has been coded using functional interfaces.

Unitary tests have been done with Mockito.

The application has been dockerized, for this, I have created a Dockerfile that compiles the application with a Gradle wrapper and runs the application. This process is orchestrated with docker-compose in case we decide to use a database or add other components to the architecture in the future.

