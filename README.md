ShopApp — Java ECommerce Application

A full-stack eCommerce web application built with **Pure Java Servlets (Jakarta EE)**, **MySQL**, and **Redis**, deployed on **Apache Tomcat**.


Project Structure

ecommerce-app/
src/main/
java/com/ecommerce/
controller/      
AuthServlet.java        (/login, /register, /logout)
HomeServlet.java        (/home)
ProductServlet.java     (/products/add, /delete, /detail)
ReviewServlet.java      (/reviews/add)
AccountServlet.java     (/account/delete)

dao/              
UserDAO.java
ProductDAO.java
ReviewDAO.java


filter/           
AuthFilter.java


model/            
User.java
Product.java
Review.java


service/           
UserService.java
ProductService.java
ReviewService.java


util/             
DBConnection.java
RedisConnection.java
JwtUtil.java


resources/
webapp/
css/
js/
WEB-INF/
views/         
web.xml
pom.xml


 Tech Stack

 Layer | Technology 
 Language | Java 11 |
 Web Framework | Jakarta EE Servlets |
 Server | Apache Tomcat 10 |
Database | MySQL 8 |
 Cache | Redis (via Jedis) |
 Auth | JWT (jjwt) + BCrypt |
 Build Tool | Maven |
 View Layer | JSP |

 Database Schema

```sql
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- Users table
CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,       -- BCrypt hashed
    role        ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    stock       INT            NOT NULL DEFAULT 0,
    created_by  INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Reviews table
CREATE TABLE reviews (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT  NOT NULL,
    product_id  INT  NOT NULL,
    rating      TINYINT CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

Relationships


- One **user** can create many **products** (admin only)
- One **user** can write many **reviews**
- One **product** can have many **reviews**
- Deleting a user first deletes their reviews (handled in `UserDAO.deleteUser()`)

### DBConnection.java Setup

```java
private static final String URL      = "jdbc:mysql://127.0.0.1:3306/ecommerce_db?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";
private static final String PASSWORD = "your_password_here";
```

### Default Admin Account

Register any account normally through the app, then run this in MySQL to promote it to admin:

```sql
UPDATE users
SET role = 'ADMIN'
WHERE email = 'your_email_here';
```



## 🔐 Security

### Authentication
- Passwords hashed with **BCrypt** before storing — never stored as plain text.
- On login a **JWT token** is generated containing `userId`, `username`, and `role`.
- Token is stored in the **HTTP Session** for browser clients.
- API clients can authenticate via `Authorization: Bearer <token>` header.

### Authorization
`AuthFilter` protects the following routes and requires a valid session or JWT:

```
/products/add
/products/delete
/account/delete
/reviews/add
```

Admin-only routes (`/products/add`, `/products/delete`) are further restricted
inside the Servlet by checking `role = ADMIN`.

---

## Redis Caching

`ProductService` caches the full product list in Redis under the key
`all_products` with a **5-minute (300s) TTL**.

```
GET /home
  └─ Redis hit?  → return cached list   
  └─ Redis miss? → query MySQL → cache result → return list
```

Cache is **invalidated** on any write operation (add / delete product)
so the next request always fetches fresh data from MySQL.

### RedisConnection.java Setup

```java
private static final String HOST     = "your_redis_host";
private static final int    PORT     = 15106;
private static final String PASSWORD = "your_redis_password";
```

---

##  Running the Project


### 1. Configure Database
```sql
CREATE DATABASE ecommerce_db;
```
Then run the full schema SQL above in MySQL Workbench.

### 2. Configure Credentials
Update `DBConnection.java` with your MySQL password.
Update `RedisConnection.java` with your Redis Cloud host, port, and password.

### 3. Build
```bash
mvn clean package
```

### 4. Deploy
```bash
copy "target\ecommerce-app.war" "C:\tomcat\webapps\"
C:\tomcat\bin\startup.bat
```

### 5. Access
```
http://localhost:8080/ecommerce-app/home
```

---

## 👤 User Roles

| Role | Permissions |
|------|------------|
| `USER` | Browse products, write reviews, delete own account |
| `ADMIN` | All USER permissions + add products, delete any product |

---

## 📌 API Endpoints

| Method | URL | Description | Auth Required |
|--------|-----|-------------|---------------|
| GET | `/home` | Home page with products & reviews | ❌ |
| GET | `/login` | Login page | ❌ |
| POST | `/login` | Submit login | ❌ |
| GET | `/register` | Register page | ❌ |
| POST | `/register` | Submit registration | ❌ |
| GET | `/logout` | Logout & clear session | ❌ |
| GET | `/products/detail?id=` | Product detail page | ❌ |
| GET | `/products/add` | Add product page | ✅ ADMIN |
| POST | `/products/add` | Submit new product | ✅ ADMIN |
| POST | `/products/delete` | Delete a product | ✅ ADMIN |
| POST | `/reviews/add` | Add a review | ✅ USER |
| POST | `/account/delete` | Delete own account | ✅ USER |

---

## 📝 Notes

- The project uses **Pure Servlets** (not Spring Boot) — all routing, session
  management, and request handling is done manually. This makes the internals
  of web frameworks transparent and is great for learning.
- Redis caching significantly reduces database load on the product listing page.
- JWT is used alongside Session to support both browser and API clients.
