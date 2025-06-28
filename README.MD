
# 🛒 E-Commerce Platform

**E-Commerce** is a modern and powerful e-commerce platform that supports product management, orders, payments, and many other features. The project focuses on clear architecture, security, and optimal user experience.

---

## 🚀 Tech Stack

| Component            | Technologies Used                                  |
|----------------------|----------------------------------------------------|
| **Backend**          | Java Spring Boot, Spring Security, Spring Data JPA |
| **Frontend**         | Next.js, React.js, Tailwind CSS, Redux Toolkit     |
| **Database**         | PostgreSQL                                         |
| **Authentication**   | JWT (JSON Web Token), OAuth2 (Google Login)        |
| **API Documentation**| Swagger (OpenAPI), Postman                         |
| **Cloud**            | IMGBB                                              |

---

## ⚙️ Installation Guide

### 🔧 Backend Setup

```bash
git clone https://github.com/bbqqvv/BackendE-Commerce.git
cd BackendE-Commerce
````

1. Update PostgreSQL connection info in `application.yaml` file
2. Run the application:

```bash
./mvnw spring-boot:run     # Using Maven Wrapper
# or
mvn spring-boot:run        # If Maven is installed on your machine
```

---

### 🐳 Run with Docker (Updating)

```bash
docker-compose up -d
```

---

## 🎯 Key Features

* ✅ Product management: CRUD products, categories, reviews
* ✅ Order management: Process orders, update status
* ✅ Shopping cart: Add, remove, update product quantities
* ✅ Payment: Integrate third-party payment gateways
* ✅ Authentication & Authorization: JWT, OAuth2 (Google), Admin/User roles
* ✅ Discount code management: Create, apply, and delete discount codes

---

## 📘 API Documentation

* 🔗 Swagger UI: [https://roway-bb6400b1f6e1.herokuapp.com/swagger-ui.html](https://roway-bb6400b1f6e1.herokuapp.com/swagger-ui.html)
* 🧾 Detailed documentation: [DeepWiki Order Management](https://deepwiki.com/bbqqvv/Backend-E-Commerce/5-order-management)
* 📥 Postman Collection: *(Add link if available)*
* 📄 API guide PDF: *(Optional attachment)*

---

## 🤝 Contribution

You are welcome to contribute by:

* **Forking** the project
* Sending **Pull Requests**
* Creating **Issues** if you have questions or find bugs

---

## 📄 License

This project is licensed under the MIT License – Feel free to use it freely, please keep the original copyright information.
