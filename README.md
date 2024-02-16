This application could help in sociological studies of the differences of the regions of the Russian Federation.
It analyzes studies conducted by RIA Rating agency (https://riarating.ru/), fills the database with them and can build a report from these data.

> Java 11, Spring Boot, Lombok, Jsoup, Apache Poi, Hibernate, Postgres, Flyway, Swagger


## Launch
To run the application, you should clone the project, build it with Gradle, and prepare a Postgres database.
You should also specify the database credentials in the config [application.yaml](src/main/resources/application.yaml).
If you haven`t, you can quickly install the database by using docker and running next command in the console, replacing some data before:
> docker run --name testdb-pg -p 5432:5432 -e POSTGRES_USER=pguser -e POSTGRES_PASSWORD=passwd123 -e POSTGRES_DB=testdb -d postgres:14.5


## API
- GET /indexation/start  - Manual start of articles indexation
- POST /research/list    - Get list of researches with pagination by filter
- POST /research/report  - Get XLS report with researches result by filter
- GET /research/researchId?researchId={id}  - Get list of regions with their places for single research
- GET /region/list       - Get list of regions with their average rating


## Search in Swagger example
![Screenshot](src/main/resources/readme/search.JPG)
![Screenshot](src/main/resources/readme/search_result.JPG)

Swagger is available on root url such as __localhost:8080/__, it will redirect to swagger url.


## Report example
![Screenshot](src/main/resources/readme/report.JPG)

You can read an example of the 2021 report here: [ria-rating-report-2021.xls](src/main/resources/readme/ria-rating-report-2021.xls)

__P.S.__ Of course, you need to remember that the RIA Rating agency is under the sanctions of the EU, UK, USA and other countries. So, be smart and separate the wheat from the chaff!
