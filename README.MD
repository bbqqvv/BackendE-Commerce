

# 🛒 E-Commerce Platform

**E-Commerce** is a modern and powerful e-commerce platform that supports product management, orders, payments, and many other features.
The project focuses on clear architecture, security, and optimal user experience.

---

## 🚀 Tech Stack

| Component             | Technologies Used                                  |
| --------------------- | -------------------------------------------------- |
| **Backend**           | Java Spring Boot, Spring Security, Spring Data JPA |
| **Frontend**          | Next.js, React.js, Tailwind CSS, Redux Toolkit     |
| **Database**          | PostgreSQL                                         |
| **Authentication**    | JWT (JSON Web Token), OAuth2 (Google Login)        |
| **API Documentation** | Swagger (OpenAPI), Postman                         |
| **Cloud**             | Cloudinary                                         |

---

## ⚙️ Backend Setup (Local)

```bash
git clone https://github.com/bbqqvv/BackendE-Commerce.git
cd BackendE-Commerce
```

1. Update PostgreSQL connection info in `application.yaml` file
2. Run the application:

```bash
./mvnw spring-boot:run     # Using Maven Wrapper
# or
mvn spring-boot:run        # If Maven is installed on your machine
```

## 🧪 Tester Guide

Tester receives file `springboot-app-1.0.tar.gz`.

### 1. Extract and load image

```bash
gunzip springboot-app-1.0.tar.gz
docker load -i springboot-app-1.0.tar
```

### 2. Run container

```bash
docker run -d --name springboot-app \
  -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/mydb \
  -e DB_USERNAME=myuser \
  -e DB_PASSWORD=mypass \
  -e MAIL_HOST=smtp.gmail.com \
  -e MAIL_USERNAME=test@gmail.com \
  -e MAIL_PASSWORD=app-password \
  -e CLOUDINARY_CLOUD_NAME=your_cloud_name \
  -e CLOUDINARY_API_KEY=your_api_key \
  -e CLOUDINARY_API_SECRET=your_api_secret \
  springboot-app:1.0
```

### 3. Check logs

```bash
docker logs -f springboot-app
```

### 4. Open in browser

👉 [http://localhost:8080](http://localhost:8080)
(or tester’s LAN IP if running on server)

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
* 🧾 Deep docs: [DeepWiki Order Management](https://deepwiki.com/bbqqvv/Backend-E-Commerce/5-order-management)
* 📥 Postman Collection: *(add link if available)*

---

## 🤝 Contribution

You are welcome to contribute by:

* **Forking** the project
* Sending **Pull Requests**
* Creating **Issues** if you have questions or find bugs

---

## 📄 License

This project is licensed under the MIT License – Feel free to use it freely, but please keep the original copyright.

