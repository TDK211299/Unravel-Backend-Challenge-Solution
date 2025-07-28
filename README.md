# Unravel-Backend-Challenge-Solution

## Overview

This repository contains a complete solution for the Unravel App Backend Developer Challenge using Java. It addresses the following key backend challenges:

1. **Thread-Safe Session Management**
2. **Memory Leak Resolution and Caching**
3. **Priority-Based Producer-Consumer Concurrency**
4. **Deadlock Prevention**
5. **Database Connection Pooling Optimization with HikariCP**

---

## Project Structure

```
├── SessionManager.java              # Thread-safe session management
├── MemoryManager.java               # Memory leak mitigation and LRU cache
├── LogProcessor.java                # Producer-Consumer task prioritization
├── LogProcessingApp.java            # Main driver for concurrency module
├── DeadlockFreeSimulator.java       # Deadlock-free locking example
├── HikariCPConfig.java              # HikariCP config and monitoring
├── README.md                        # This file
```

---

## Setup Instructions

### Prerequisites

- Java 11+
- Maven or Gradle
- MySQL (or compatible RDBMS)
- Spring Boot (for HikariCP integration)

### Database Setup

1. Create a database named `mydb`.
2. Add user `dbuser` with password `dbpassword`.
3. Update credentials in `HikariCPConfig.java` as needed.

### Maven Dependency

Add to your `pom.xml`:

```xml
<dependency>
  <groupId>com.zaxxer</groupId>
  <artifactId>HikariCP</artifactId>
  <version>5.0.1</version>
</dependency>
```

---

##  Execution

### Compile & Run

```bash
javac *.java
java LogProcessingApp
```

For Spring Boot apps:

```bash
./mvnw spring-boot:run
```

---

## Features

### Session Management

- Thread-safe login/logout with `ReentrantLock`
- Uses `ConcurrentHashMap`
- Robust exception handling

### Memory Management

- Efficient session data caching with LRU eviction
- Prevents out-of-memory errors during load

### Priority-based Producer-Consumer

- Uses `PriorityBlockingQueue`
- Dynamically prioritizes tasks
- Avoids starvation of low-priority tasks

### Deadlock Prevention

- Avoids deadlocks via `tryLock()` and lock ordering
- Demonstrates concurrency-safe locking strategy

### HikariCP Monitoring

- Logs active, idle, and waiting DB connections
- Enables smart connection pool tuning

---

## File Documentation

| File | Description |
|------|-------------|
| `SessionManager.java` | Thread-safe session login/logout and retrieval |
| `MemoryManager.java` | 10MB/session LRU memory cache |
| `LogProcessor.java` | Producer/consumer with priority handling |
| `DeadlockFreeSimulator.java` | Simulates and prevents deadlock |
| `HikariCPConfig.java` | Configures and monitors HikariCP |

---

## Optional Enhancements

- [ ] Add unit/integration tests (JUnit)
- [ ] Add session expiration (TTL)
- [ ] Export HikariCP metrics via Actuator
- [ ] Use Redis for distributed session store

---

## Author

**Aryan Jain**

---

## License

MIT – Feel free to use and extend this code for learning and demonstration purposes.
