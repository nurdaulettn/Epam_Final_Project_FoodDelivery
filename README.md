# AsapJe Food Delivery

AsapJe is a Spring MVC web application for food delivery. It supports customer ordering, restaurant manager menu and order management, and administrator restaurant/order control.

## Tech Stack

- Java 21
- Maven
- Spring Core, Spring MVC, Spring Security, Spring JDBC
- Thymeleaf
- PostgreSQL
- JUnit 5 and Mockito
- SLF4J with Logback
- Custom JDBC connection pool

## Main Features

- User registration and login
- Role-based access: `CUSTOMER`, `MANAGER`, `ADMIN`
- Restaurant browsing and search
- Food browsing with category, restaurant, and search filters
- Restaurant details with customer reviews and rating
- Favorite foods
- Cart and checkout flow
- Delivery and pickup order types
- Mock payment button that moves an order to preparing
- Customer order history and order details
- Manager food management
- Manager order list, order details, and status updates
- Manager order item details
- Admin user management: list, block, unblock, delete, and role update
- Admin restaurant approval/rejection
- Admin order overview
- Admin category management
- Pagination for long lists
- EN/RU internationalization with cookie-based language persistence
- Centralized exception handling
- Centralized request and business event logging
- Duplicate form submission protection for critical actions
- DAO and service unit tests
- Javadoc on service interfaces

## Project Structure

```text
src/main/java/kz/nurdaulet
  config        Spring, security, i18n, error handling, database config
  controller    Spring MVC controllers
  dao           DAO interfaces and JDBC implementations
  dto           Request/view DTOs
  entity        Domain entities and enums
  exception     Custom exceptions
  facade        Higher-level business facade logic
  service       Service interfaces and implementations
  validation    Custom validators

src/main/resources
  db_scripts    Database schema and seed data
  templates     Thymeleaf pages
  static        CSS and static assets
```

## Prerequisites

- JDK 21
- Maven 3.9+
- PostgreSQL
- Apache Tomcat with Jakarta Servlet support

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE asapje_db;
```

Configure database access in:

```text
src/main/resources/application.properties
```

Default local configuration:

```properties
database.driver=org.postgresql.Driver
database.url=jdbc:postgresql://localhost:5432/asapje_db
database.username=postgres
database.password=nurdau50
```

Run scripts in this order:

```text
src/main/resources/db_scripts/init_db.sql
src/main/resources/db_scripts/data.sql
```

Example with `psql`:

```bash
psql -U postgres -d asapje_db -f src/main/resources/db_scripts/init_db.sql
psql -U postgres -d asapje_db -f src/main/resources/db_scripts/data.sql
```

## Seed Users

The seed data creates these users:

| Role | Username | Email |
| --- | --- | --- |
| ADMIN | `admin` | `admin@gmail.com` |
| MANAGER | `manager` | `manager@gmail.com` |
| CUSTOMER | `alice` | `alice@gmail.com` |

The seeded password hash is the same for all three users. Use the project seed password configured by the author for local demo data.

## Build and Test

Run all tests:

```bash
mvn test
```

Build the WAR file:

```bash
mvn package
```

The generated artifact is:

```text
target/asap-je.war
```

## Run

Deploy the WAR to Tomcat. After deployment, open:

```text
http://localhost:8080/asap-je/
```

The final context path depends on your Tomcat deployment configuration.

## Key Routes

Public:

- `/`
- `/login`
- `/register`
- `/restaurants`
- `/foods`

Customer:

- `/cart`
- `/cart/checkout`
- `/orders`
- `/orders/{orderId}`
- `/favorites`
- `/restaurants/{restaurantId}`
- `/restaurants/{restaurantId}/reviews`

Manager:

- `/restaurants/manager/my-restaurants`
- `/restaurants/manager/create`
- `/restaurants/manager/my-restaurants/{restaurantId}`
- `/restaurants/manager/{restaurantId}/orders`
- `/restaurants/manager/{restaurantId}/orders/{orderId}`

Admin:

- `/admin/users`
- `/admin/create-requests`
- `/admin/orders`
- `/categories`
- `/admin/categories/create`

## Role Capabilities

`CUSTOMER` can browse restaurants and foods, use the cart, choose delivery or pickup, pay with a mock payment button, track orders, add favorite foods, and leave restaurant reviews.

`MANAGER` can create restaurant requests, manage restaurant foods, view restaurant orders, inspect order items, and update order statuses through allowed transitions.

`ADMIN` can approve or reject restaurant requests, manage users, view all orders, and manage categories.

## Localization

The application supports English and Russian:

- `src/main/resources/messages_en.properties`
- `src/main/resources/messages_ru.properties`

Language is changed with the `lang` request parameter and stored in a cookie.

Example:

```text
/foods?lang=en
/foods?lang=ru
```

## Architecture and Patterns

The project follows layered MVC architecture:

- Controller layer handles HTTP requests and view models.
- Service layer contains business rules and transaction boundaries.
- DAO layer isolates JDBC queries and database access.
- Thymeleaf templates render the UI.

Used patterns:

- MVC Pattern: Spring MVC controllers, models, and Thymeleaf views separate web concerns.
- DAO Pattern: each DAO hides SQL and JDBC details behind an interface.
- Dependency Injection: Spring manages services, DAOs, validators, and configuration objects.
- Facade Pattern: `ManagerFoodFacade` coordinates manager food operations across restaurant, category, and food services.
- Interceptor Pattern: `RequestLoggingInterceptor` handles request logging as a cross-cutting concern.

## Validation and Error Handling

The application uses server-side validation with Bean Validation annotations and Spring `BindingResult` handling. The UI also uses browser validation attributes such as `required`, typed inputs, length limits, and numeric limits where appropriate.

Errors are handled centrally by `GlobalExceptionHandler`, which returns user-friendly error pages and logs technical details through SLF4J/Logback.

## Logging

Logging is configured in:

```text
src/main/resources/logback.xml
```

Logging levels are used consistently:

- `info` for important business events;
- `warn` for rejected operations and business rule violations;
- `debug` for technical details and frequent low-level operations;
- `error` for unexpected failures.

HTTP request logging is handled by a Spring MVC interceptor.

## Duplicate Submission Protection

The project uses the POST-Redirect-GET pattern for form submissions and disables critical submit buttons on the frontend after submission. Backend operations such as payment and manager status updates are also idempotent where repeated submissions could happen.

## Testing

Unit tests are located under:

```text
src/test/java
```

Covered areas:

- DAO layer
- Service layer
- Manager food facade

The tests use:

- JUnit 5
- Mockito
- `given / when / then` structure
- `verify(...)` for mocked dependencies

Current test suite status:

```text
Tests run: 108, Failures: 0, Errors: 0, Skipped: 0
```

## Requirements Coverage

- Spring Core, Spring MVC, Spring Security, and Spring JDBC are used.
- The project follows Controller -> Service -> DAO layering.
- PostgreSQL stores domain data.
- Plain JDBC/Spring JDBC is used; ORM frameworks are not used.
- Passwords are stored as BCrypt hashes.
- SQL scripts are provided under `src/main/resources/db_scripts`.
- Transactions are used for critical order and review operations.
- i18n is implemented with English and Russian resource bundles.
- Pagination is implemented for long customer, manager, and admin lists.
- Centralized logging and exception handling are configured.
- DAO and service layers are covered by unit tests.
- Service interfaces contain Javadoc comments.

## Notes

- Passwords are stored as BCrypt hashes.
- Database access is implemented with Spring JDBC and a custom connection pool.
- ORM frameworks are not used.
- Order creation is transactional.
- Payment is mocked: clicking the pay button changes the order status to `PREPARING`.
- Real payment provider integration is intentionally out of scope.
