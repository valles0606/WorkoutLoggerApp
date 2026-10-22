# Project Overview 📖 #
A RESTful backend for a personal workout tracker, built with Java and Spring Boot. 
The project grew from wanting a customizable way to track gym progress without relying on subscription-based apps. 
It organizes workouts, exercises, and sets, with support for workout notes.

# Current Features 📝 #
- Create, view, update, and delete workouts.
- Add exercises to workouts and retrieve exercises within their parent workout.
- Add exercise sets to exercises and retrieve sets within their parent exercise.
- Validate incoming requests.
- Store workout data in PostgreSQL.

# Technology Stack 💻 #
| Technology | Purpose |
|---|---|
| Java and Spring Boot | Backend application and REST endpoints |
| Spring Data JPA / Hibernate | Database persistence and entity relationships |
| PostgreSQL | Workout data storage |
| Docker | Local PostgreSQL container |
| Maven | Build and dependency management |
| Postman | Manual endpoint testing |

# Data Model 📊 #


# API Endpoints 📲 #
| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/v1/workouts` | Create a workout |
| `GET` | `/api/v1/workouts` | List workouts |
| `GET` | `/api/v1/workouts/{workoutId}` | Get a workout |
| `PUT` | `/api/v1/workouts/{workoutId}` | Update a workout |
| `DELETE` | `/api/v1/workouts/{workoutId}` | Delete a workout |
| `POST` | `/api/v1/workouts/{workoutId}/exercises` | Add an exercise |
| `GET` | `/api/v1/workouts/{workoutId}/exercises/{exerciseId}` | Get an exercise |

# Roadmap + Future Plans 💭 #
- Complete exercise set management.
- Containerize the Spring Boot application.
- Add automated service and controller tests.
- Build a web or mobile frontend.
- Add workout history and progress summaries.
