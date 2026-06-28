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
- Cart and checkout flow
- Delivery and pickup order types
- Mock payment button that moves an order to preparing
- Customer order history and order details
- Manager food management
- Manager order list, order details, and status updates
- Admin restaurant approval/rejection
- Admin order overview
- Pagination for long lists
- EN/RU internationalization with cookie-based language persistence
- Centralized exception handling
- DAO and service unit tests

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

Manager:

- `/restaurants/manager/my-restaurants`
- `/restaurants/manager/create`
- `/restaurants/manager/my-restaurants/{restaurantId}`
- `/restaurants/manager/{restaurantId}/orders`
- `/restaurants/manager/{restaurantId}/orders/{orderId}`

Admin:

- `/admin/create-requests`
- `/admin/orders`
- `/admin/categories/create`

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

## Notes

- Passwords are stored as BCrypt hashes.
- Database access is implemented with Spring JDBC and a custom connection pool.
- ORM frameworks are not used.
- Order creation is transactional.
- Payment is mocked: clicking the pay button changes the order status to `PREPARING`.
