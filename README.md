# Interconnecting-flights
#### Deploy project with docker (On the root of the project "/interconnecting-flights"):

`$ docker-compose up`



**Interesting endpoints:**


`Swagger Interface` : <http://localhost:8080/swagger-ui.html#>

`Endpoint` : http://localhost:8080/api/interconnections?params...

`Endpoint example` : http://localhost:8080/api/interconnections?departure=MAD&arrival=DUB&departureDateTime=2021-01-29T17%3A00&arrivalDateTime=2021-03-03T21%3A00

**Considerations:**

Spring Boot 2, Java11, and Swagger have been used for this project.

The code has been organised with a controller to receive the requests and a service to do the business logic. The requests to other microservices are in services facades. The main idea to have all this services is apply the Single-responsibility principle and decouple the code.

Most of the business logic in the InterconnectionService have been coded using functional interfaces.

Unitary tests have been done with Mockito. The tests are executed with `$ docker-compose up`

The application has been dockerized, for this I have created a Dockerfile that compiles the application with a Gradle wrapper and runs the application. This process is orchestrated with docker-compose in case we decide to use a database or add other components to the architecture in the future.



TODO:Unitary tests
Finish Readme