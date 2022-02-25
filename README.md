# RaspiSurveillance - Server

A server for surveillance on a RaspiberryPi.

## Copyright

2020-2022 Denis Meyer, https://github.com/CallToPower

## Concept

### Roles

- Roles for API authentication
- Current roles for authentication are ADMIN and USER

### Users

- Users have a role

### Servers

- Master server(s)
  - for startup/shutdown of wifi electric sockets
  - project Raspi-SAPIS configured as master
- Normal Server(s)
  - host the services (camerastream/surveillance)
  - project Raspi-SAPIS configured as normal server

## Installation

This software is provided via docker. More information in the producation and development documentation.

Available docker images:

- calltopower/raspisurveillance-flyway:1.2.1
- calltopower/raspisurveillance-db:1.2.1
- calltopower/raspisurveillance-server:1.2.1
- calltopower/raspisurveillance-ng:1.2.1

The initially created user/password combination is: Admin/password1

## Production and development documentation

See README-DEV.md.
