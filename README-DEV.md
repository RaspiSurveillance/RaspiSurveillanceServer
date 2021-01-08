# RaspiSurveillanceServer

Based on Java, Spring boot, Spring security (token-based authentication), docker, MariaDB and more.

## Requirements

### Docker

#### Installation

- Instructions: https://hub.docker.com/editions/community/docker-ce-desktop-windows/

## Build for production

### Build the RaspiSurveillance database image

- "docker image build -t raspisurveillance-db -f docker/db/Dockerfile ."
- "docker login"
- "docker tag raspisurveillance-db <dockerName>/raspisurveillance-db:<version>"
- "docker push <dockerName>/raspisurveillance-db:<version>"

### Build the RaspiSurveillance database migration image

- "docker image build -t raspisurveillance-flyway -f docker/flyway/Dockerfile ."
- "docker login"
- "docker tag raspisurveillance-flyway <dockerName>/raspisurveillance-flyway:<version>"
- "docker push <dockerName>/raspisurveillance-flyway:<version>"

### Build the RaspiSurveillance server

- "docker image build -t raspisurveillance-server -f docker/raspisurveillanceserver/Dockerfile ."
- "docker login"
- "docker tag raspisurveillance-server <dockerName>/raspisurveillance-server:<version>"
- "docker push <dockerName>/raspisurveillance-server:<version>"

## Run the software

Windows: On windows the user has to add the project folder in file sharing in order to activate bind mounting into Docker containers:

- Docker - Settings - Resources - + - Select "/path/to/RaspiSurveillanceServer"

### Local development

- Start via "docker-compose -f docker/stack-dev.yml up"
- Database UI available at http://localhost:8080
- You might want to insert some example data: "resources/database.sql" + "resources/example-data.sql"
- Set the spring profile to "dev", e.g. in Eclipse: "Run"-"Run configurations"-<ConfigName>-"Arguments"-"Program Arguments": "-Dspring.profiles.active=dev"
- Start STDApplication.java as a Java application

### Production

- Change usernames, passwords, API_URL, etc. in "docker/stack.yml"
- Change the nginx configuration at "docker/nginx/nginx.prod.conf" and copy it over to "docker/nginx/nginx.conf" on your server
- Change the flyway db migration configuration at "docker/flyway/config/flyway.conf" and copy it over to "docker/flyway/config/flyway.conf" on your server
- Generate an ssl certificate for your domain, e.g. via https://letsencrypt.org
- Make sure to have the correct permissions for the mounted database folder: "sudo chown -R 1001:1001 data/db/"
- Start via "docker-compose -f docker/stack.yml up"

### Infos

- The database is externalized and writes data to "docker/data/db" so it's not erased on container deletion.

## Stop the software

- Stop the spring application if started
- Stop docker containers (e.g. "docker-compose -f ./docker/stack-dev.yml stop" or "docker-compose -f ./docker/stack.yml stop")

## Some useful docker commands

### List images

docker images

### Delete an image

docker rmi imgId1[, imgId2, ...]

### List all containers

docker container ls -a

### Delete a container

docker container rm cId

## Authentication

When first started, an admin user is set up (local development and production) with username/password combination: Admin/password1

### 1. Sign up a user

POST http://localhost:9090/api/auth/signup
{
    "username": "NewUser",
    "email": "newuser@mail.com",
    "password": "password1"
}

### 2. Sign in the user

POST http://localhost:9090/api/auth/signin
{
    "username": "NewUser",
    "password": "password1"
}

-> Copy the authentication token <token>

### 3. Authenticate the following requests with the token

GET http://localhost:9090/api/<path>
[HEADER] Authorization: Bearer <token>

## More information

Properties and configuration in example files:

* docker/stack.yml
