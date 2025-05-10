
# 🛒 **E-Commerce Platform**

**E-Commerce** là một nền tảng thương mại điện tử hiện đại, mạnh mẽ, hỗ trợ quản lý sản phẩm, đơn hàng, thanh toán và nhiều tính năng khác. Dự án tập trung vào kiến trúc rõ ràng, bảo mật và trải nghiệm người dùng tốt.

---

## 🚀 **Tech Stack**

| Thành phần            | Công nghệ sử dụng                                  |
| --------------------- | -------------------------------------------------- |
| **Backend**           | Java Spring Boot, Spring Security, Spring Data JPA |
| **Frontend**          | Next.js, React.js, Tailwind CSS, Redux Toolkit     |
| **Database**          | PostgreSQL                                         |
| **Authentication**    | JWT (JSON Web Token), OAuth2 (Google Login)        |
| **API Documentation** | Swagger (OpenAPI), Postman                         |

---

## ⚙️ **Hướng Dẫn Cài Đặt**

### 🔧 **Cài Đặt Backend**

```bash
git clone https://github.com/bbqqvv/BackendE-Commerce.git
cd BackendE-Commerce
```

1. Cập nhật thông tin kết nối **PostgreSQL** trong `application.yaml`
2. Chạy ứng dụng:

```bash
./mvnw spring-boot:run     # Nếu dùng Maven Wrapper
mvn spring-boot:run        # Nếu đã cài Maven
```

---

### 🐳 **Chạy bằng Docker (Đang cập nhật)**

```bash
docker-compose up -d
```

---

## 🎯 **Tính Năng Chính**

* ✅ Quản lý sản phẩm: CRUD sản phẩm, danh mục, đánh giá
* ✅ Quản lý đơn hàng: Xử lý đơn, cập nhật trạng thái
* ✅ Giỏ hàng: Thêm, xóa, cập nhật số lượng
* ✅ Thanh toán: Hỗ trợ cổng thanh toán bên thứ ba
* ✅ Xác thực & phân quyền: JWT, OAuth2 (Google), Admin/User
* ✅ Quản lý mã giảm giá: Tạo, áp dụng, xóa mã giảm giá

---

## 📘 **Tài Liệu API**

* 🔗 Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* 🧾 Tài liệu chi tiết: [DeepWiki Order Management](https://deepwiki.com/bbqqvv/BackendE-Commerce/5-order-management)
* 📥 Postman Collection: *(Thêm link nếu có)*
* 📄 PDF hướng dẫn API: *(Tùy chọn đính kèm)*

---

## 🤝 **Đóng Góp**

Bạn có thể:

* **Fork** dự án
* Gửi **Pull Request**
* Tạo **Issue** nếu có câu hỏi hoặc lỗi

---

## 📄 **License**

MIT License – Sử dụng thoải mái, vui lòng giữ bản quyền tác giả.

---

> 🚀 *Chúc bạn học tập & phát triển dự án thật vui vẻ!* 😎

---

Bạn muốn mình chuyển phần này sang file `README.md` hoặc định dạng khác như PDF hay HTML không?
