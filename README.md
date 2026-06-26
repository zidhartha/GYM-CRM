## Microservices

Based on the codebase created during the previous module, implement follow functionality

### Description

The main idea behind the creation of this microservice is as follows.

Every time a training session is planned or cancelled for a trainer; this data is transferred to a newly created microservice.

The Microservice calculates the number of working hours of each trainer for each month and stores this information in in-memory database.

In addition, when requesting the number of training hours from any of the trainers in a particular month, the microservice retrieves this data from the database and returns it to the requester.

---

## Requirements

1. Implement separate Spring boot Application (Microservice).

2. Application should implement REST endpoint to accept trainer's workload with follow contract:

### Request

1. Trainer Username
2. Trainer First Name
3. Trainer Last Name
4. IsActive
5. Training date
6. Training duration
7. Action Type (ADD/DELETE)

### Response

1. 200 OK

---

3. Implement service function corresponding to mentioned below REST endpoint. Service should calculate as in-memory saved structure (in memory DB) trainer's monthly summary of the provided trainings. The model should be the follow:

4. Trainer Username

5. Trainer First Name

6. Trainer Last Name

7. Trainer Status

8. Years (List)

    1. Months (List)

        1. Training summary duration

---

4. Update Existing Main Microservice implementation to call Secondary Microservice every time that new training added or deleted to the system.

5. Implement discovery module according to guide Eureka Discovery Service.

6. Use circuit breaker design pattern in your implementation.

7. Implement Authorization − Bearer token for Microservices integration Use JWT token implementation.

8. Two levels of logging should be implemented - transactions and each operation transaction level - which endpoint was called, which request came and the service response - 200 or error and response message + at this level, a transactionId is generated, by which you can track all operations for this transaction the same transactionId can later be passed to downstream services.

---

https://www.baeldung.com/spring-cloud-netflix-eureka

---

## Notes

1. For REST API implementation use second level of Richardson maturity model.

2. Try to understand in which case training can be deleted.

---

## Running with Docker

Ports: main `8080`, workload `8081`, auth `8083`, eureka `8761`, activemq console `8161`, postgres `5432`, mongo `27017`.

> Run all commands from the project root. First build pulls images and compiles all modules, so it takes a few minutes.

### Start clean (run once)

```bash
docker compose -f docker-compose.yml -f docker-compose.integration.yml down -v
```

Use `-v` only here. It wipes the volumes so Postgres re-initializes the `GYM-CRM` database. (Reusing an old volume causes `FATAL: database "GYM-CRM" does not exist`.)

### Subtask 1 — build images and run WITHOUT integrations

Runs only `gym-crm-main`, `gym-crm-workload` and their databases. Eureka, ActiveMQ and the auth server are disabled via the `disabled` Spring profile.

```bash
docker compose up -d --build
docker compose ps
```

Verify (wait ~60s for `gym-crm-main` to become `healthy`):

```bash
curl http://localhost:8080/actuator/health
```

Pass criteria:
- `docker compose ps` lists only `postgres`, `mongodb`, `gym-crm-main`, `gym-crm-workload`.
- Health returns `"status":"UP"` with a `db` component and **no** `eureka`/`jms` components.

### Subtask 2 — run WITH integrations enabled (DB + queue + discovery)

Adds ActiveMQ, Eureka and the auth server, and switches the apps to the `default` profile.

```bash
docker compose -f docker-compose.yml -f docker-compose.integration.yml up -d --build
docker compose -f docker-compose.yml -f docker-compose.integration.yml ps
```

Verify (wait ~60s):

```bash
curl http://localhost:8080/actuator/health
```

Pass criteria:
- All 7 services are `Up` / `healthy`.
- Health returns `"status":"UP"` with `db`, `jms` (ActiveMQ) and `eureka` all `UP`.
- Eureka dashboard at http://localhost:8761/ lists `GYM-CRM-MAIN`, `WORKLOAD-SERVICE`, `AUTH-SERVICE`.

### Subtask 3 — check logs and open a shell in the containers

```bash
# Logs
docker compose -f docker-compose.yml -f docker-compose.integration.yml logs gym-crm-main --tail 50
docker compose -f docker-compose.yml -f docker-compose.integration.yml logs gym-crm-workload --tail 50

# Shell inside a running container
docker exec -it gym-crm-main bash
#   then: ls /app   and   exit
```

### Stop

```bash
# keep database data
docker compose -f docker-compose.yml -f docker-compose.integration.yml down

# wipe database data too
docker compose -f docker-compose.yml -f docker-compose.integration.yml down -v
```
