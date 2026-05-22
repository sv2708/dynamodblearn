# Agent Instructions

## Project Overview

This is a Quarkus Java service built with Gradle Kotlin DSL. The current application exposes a REST endpoint from `src/main/java/org/sv2708/GreetingResource.java`, with Quarkus tests under `src/test/java` and native/integration tests under `src/native-test/java`.

The project uses Java 25:

- `sourceCompatibility = JavaVersion.VERSION_25`
- `targetCompatibility = JavaVersion.VERSION_25`

## Repository Layout

- `build.gradle.kts` - Gradle build, Quarkus plugin, dependencies, and Java settings.
- `gradle.properties` - Quarkus platform coordinates and Gradle properties.
- `settings.gradle.kts` - Gradle project settings.
- `src/main/java` - Application source code.
- `src/main/resources` - Runtime configuration.
- `src/test/java` - JVM tests.
- `src/native-test/java` - Quarkus integration/native test classes.
- `src/main/docker` - Quarkus-generated Dockerfiles for JVM, legacy JAR, and native images.
- `docker` - Local Docker Compose/DynamoDB Local assets.

## Common Commands

Use the Gradle wrapper from the repository root:

```bash
./gradlew quarkusDev
./gradlew test
./gradlew build
```

For native builds:

```bash
./gradlew build -Dquarkus.native.enabled=true
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

The dev UI is available only while `quarkusDev` is running:

```text
http://localhost:8080/q/dev/
```

## Development Guidelines

- Keep source changes scoped to the existing Quarkus structure unless a feature clearly needs new packages.
- Prefer constructor injection or Quarkus-supported CDI patterns for new services.
- Add or update tests with behavior changes. For REST endpoints, use `@QuarkusTest` and Rest Assured as shown in `GreetingResourceTest`.
- Keep package names under `org.sv2708` unless there is a deliberate package reorganization.
- Do not commit generated build outputs from `build/`, `.gradle/`, or compiled classes.
- Use `application.properties` for Quarkus configuration rather than hardcoding runtime values.
- If adding DynamoDB behavior, keep local-development settings separate from production-facing configuration and document any Docker Compose requirement in `README.md`.

## Verification Expectations

Before handing off code changes, run the narrowest useful verification:

```bash
./gradlew test
```

Run `./gradlew build` when changes affect packaging, Dockerfiles, native-test setup, Gradle configuration, or application startup behavior.

If tests cannot be run because Java 25, Docker, network access, or dependencies are unavailable, state the exact blocker in the final response.
