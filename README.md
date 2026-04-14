# SOEN345_CacheMoney
The Ticket Reservation Application is a Java-based web system designed to simplify the process of browsing, reserving, and managing event tickets. It enables users to explore a wide range of events while providing features to search and filter events by date, location, or category. Users can register using their email or phone number, reserve tickets, and receive digital confirmations by email upon successful booking.

The system is designed to serve two main user groups: customers and event administrators. While customers can manage their reservations and receive confirmations via email, administrators are responsible for creating, updating, and canceling events. Built with scalability and usability in mind, the application supports concurrent users and provides a simple, user-friendly interface for an efficient booking experience.

## E2E Tests (Maestro)

**Install Maestro:** https://docs.maestro.dev/get-started/quickstart

**Prerequisites**
- Android emulator running (or device connected via ADB)
- Backend running and reachable from the device

**Run all tests**
```bash
maestro test .\.maestro\
```

## Setup

Clone the repository and follow the steps:

**Backend (Spring Boot)**
### 1. Requirements
- Java 17+
- Maven (wrapper included)

### 2. Configuration
The backend uses PostgreSQL (Supabase) and Gmail SMTP for mail services. **Do not edit `application.properties` directly.**

Instead, copy the example environment file and fill in your credentials:

```sh
cd backend
cp .env.example .env
# On Windows, use: copy .env.example .env
```

Edit the new `.env` file to provide your database and mail credentials. Example:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://your-host:5432/your-db?sslmode=require
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-gmail-app-password
```

The backend will automatically load these values at startup.

### 3. Run the backend

- On Windows
```
cd backend
.\mvnw spring-boot:run
```

- On Linux/macOS
```
cd backend
./mvnw spring-boot:run
```

---

**Frontend (Android)**

### 1. Requirements
- Android Studio (Recommended)
- JDK 11+
- Gradle (wrapper included)

### 2. Build and Run
- Open the *client* folder in Android Studio
- Use the Gradle wrapper or Android Studio's build/run buttons
- Minimum SDK: 24, Target SDK: 36


