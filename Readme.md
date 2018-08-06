# OAuth2 Example 

I will be following [developers guide](https://projects.spring.io/spring-security-oauth/docs/oauth2.html) and looking into integration tests and sample applications that are linked on that page.

Authorization Service and Resource Service will be on same server for this example. I will also configure Authorization Service to use JWT.

WARNING: New JWT token has to be retrieved after every restart of the server. 

## Docker database
```bash
docker run -it --rm --name oauth2-postgres -p 5432:5432 -e POSTGRES_USER=oauth2 -e POSTGRES_PASSWORD=oauth2 postgres
```

## Testing

### Testing controllers

When writing unit tests you should follow examples in this [web page](https://spring.io/guides/gs/testing-web/).  For mocking spring security there is this [web page](https://docs.spring.io/spring-security/site/docs/4.0.x/reference/htmlsingle/#test)

Services and repositories should be tested with unit tests and REST controllers should be tested with integration tests. 

## Quality control

### Jacoco test coverage 

You can run `./mvnw clean generate-sources verify` and read site that was created targe/site directory.

### Find Bugs

Report will be created with `./mvnw site`

### PMD 

Report will be created with `./mvnw site`

### Checkstyle

Report will be created with `./mvnw site`

### Error Prone

Compile task will fail if errors are found

## Checking Hibernate schema creation

You need to comment out flyway dependency in pom.xml, and uncomment `hibernate.ddl-auto: create` in application.yml.  

## Documenting API
API documentation is created by Spring REST Docs. You can generate static documentation an package it in jar by running `./mvnw clean verify package`or create it in site with `./mvnw clean verify site` 

## TODO
- [x] Add tests for /public, /private and /actuator/health end points
- [x] Configure server to work only with HTTPS protocol
- [x] Add support for retrieving user information from database
- [x] Add support for retrieving client information from database (web clients)
