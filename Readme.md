# OAuth2 Example 

I will be following [developers guide](https://projects.spring.io/spring-security-oauth/docs/oauth2.html) and looking into integration tests and sample applications that are linked on that page.

Authorization Service and Resource Service will be on same server for this example. I will also configure Authorization Service to use JWT.

WARNING: New JWT token has to be retrieved after every restart of the server. 

## Docker database
```bash
docker run -it --rm --name oauth2-postgres -e POSTGRES_USER=oauth2 -e POSTGRES_PASSWORD=oauth2 postgres
```

## TODO
- [x] Add tests for /public, /private and /actuator/health end points
- [x] Configure server to work only with HTTPS protocol
- [x] Add support for retrieving user information from database
- [ ] Add support for retrieving client information from database (web clients)
