# RaspiSurveillanceServer - Changelog

## v1.5.0

- Dependency upgrades, most notable
	- mariadb 10.6 -> 10.10
	- flyway 8.5 -> 9.16
	- openjdk 17 -> eclipse-temurin 17
	- SpringBoot 2 -> SpringBoot 3
	- Hibernate 5 -> Hibernate 6
	- Angular 13 -> Angular 15
	- Bootstrap 12 -> 14
	- nginx 1.21 -> 1.23

## v1.4.3

- Reset server status
- Hard shutdown

## v1.4.2

- Visual and handling optimizations

## v1.4.1

- Added missing i18n
- Replaced the notification service with a library

## v1.4.0

- Java 17
- Gradle 7.4.1
- Spring 2.5.11
- Flyway 8.5.5
- More dependency updates

## v1.3.1

- Fixed styling issues

## v1.3.0

- Version Upgrades (done)
	- Database
	- Database Migrations
	- Gradle
	- Java
	- Angular

## v1.2.2

- Fixed styling issues

## v1.2.1

- Upgrade to Java 11
- Update dependencies
- Upgrade to Angular 13

## 1.2.0

- New status: Initializing (directly after creating it)
- Faster create and update processes
- Less mandatory initial server fields
- Add/Edit server optimized
- Added surveillance attributes to the PUT request

## 1.1.2

- Improved status checks

## v1.1.1

- Added request timeouts
- New backend parameter: RASPISURVEILLANCE_REQUESTS_TIMEOUT

## v1.1.0

- Added master server for startup-shutdown (via Raspi-SAPIS api)
- Added service overview (camerastream/surveillance)
- Added new status starting, stopping
- Bug fixing

## v1.0.0

- Initial version
