# TaskInMind - Backend

This is the backend for **TaskInMind**, a full-stack calendar and task management application. It's a robust RESTful API built with Java and Spring Boot.

**Frontend Repository:** https://github.com/ipsman/TaskInMind-Frontend

---

### Features

* Secure user authentication and authorization with JWT and Spring Security.
* Complete CRUD (Create, Read, Update, Delete) operations for tasks and calendar events.
* RESTful API endpoints for seamless integration with any frontend client.
* Relational database schema designed for efficiency and scalability.

---

### Tech Stack

* **Java 21** 
* **Spring Boot**
* **Spring Security** for authentication & authorization
* **Spring Data JPA / Hibernate** for database communication
* **MySQL** for the relational database
* **Maven** for project management
* **JUnit & Mockito** for unit and integration testing
* **Lombok** to reduce boilerplate code

---

### Getting Started

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/ipsman/taskInMind-backend.git](https://github.com/ipsman/taskInMind-backend.git)
    ```
2.  **Configure the database:**
    * Open `src/main/resources/application.properties`.
    * Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties with your local MySQL credentials.
3.  **Run the application:**
    ```sh
    ./mvnw spring-boot:run
    ```
4. The API will be available at `http://localhost:8080`.

---

### API Endpoints

A sample of the available endpoints:

* `POST /api/auth/register` - Register a new user
* `POST /api/auth/login` - Authenticate a user
* `GET /api/tasks` - Get all tasks for the authenticated user
* `POST /api/tasks` - Create a new task
