
---

# 🛒 **Dự Án E-Commerce**

🚀 **E-Commerce** là một nền tảng thương mại điện tử **hiện đại** và **mạnh mẽ**, giúp quản lý sản phẩm, đơn hàng, thanh toán và nhiều tính năng khác.

---

## 📌 **Công Nghệ Sử Dụng**
| Thành phần  | Công nghệ  |
|-------------|------------|
| **Backend**  | Java Spring Boot, Spring Security, Spring Data JPA  |
| **Frontend**  | Next.js, React.js, Tailwind CSS, Redux Toolkit  |
| **Database**  | PostgreSQL  |
| **Authentication** | JWT (JSON Web Token), OAuth2 (Google Login)  |

---

## 📥 **Hướng Dẫn Cài Đặt**

### 🚀 **Cài Đặt Backend**
1. **Clone dự án:**
   ```sh
   git clone https://github.com/bbqqvv/BackendE-Commerce.git
   cd BackendE-Commerce
   ```

2. **Cấu hình database:**
   - Mở file `application.yaml`
   - Cập nhật thông tin kết nối **PostgreSQL**

3. **Chạy ứng dụng:**
   ```sh
   ./mvnw spring-boot:run  # Nếu dùng Maven Wrapper
   mvn spring-boot:run      # Nếu có Maven cài sẵn
   ```

---

### 🐳 **Chạy với Docker (Sẽ cập nhật sau)**
```sh
docker-compose up -d
```

---

## 🎯 **Tính Năng Chính**
✅ **Quản lý sản phẩm:** CRUD sản phẩm, danh mục, đánh giá  
✅ **Quản lý đơn hàng:** Xử lý đơn hàng, cập nhật trạng thái  
✅ **Giỏ hàng:** Thêm/xóa sản phẩm, cập nhật số lượng  
✅ **Thanh toán:** Hỗ trợ thanh toán qua cổng thứ ba  
✅ **Xác thực & Phân quyền:** JWT, OAuth2 (Google), phân quyền Admin/User  
✅ **Quản lý mã giảm giá:** Tạo, áp dụng và xóa mã giảm giá

---

## 📖 **Tài Liệu API**
📝 Bạn có thể xem tài liệu API chi tiết tại:

🔗 **Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) *(Nếu đã bật Swagger)*

📘 **Postman Collection:** *(Thêm link Postman nếu có)*

📂 **Hướng dẫn API chi tiết:** *(Có thể đính kèm file PDF hoặc README riêng cho API)*

---

## 💡 **Đóng Góp**
🎯 Mọi người có thể **fork** và gửi **pull request** để đóng góp vào dự án.  
📩 Nếu có bất kỳ câu hỏi nào, hãy tạo **issue** trên GitHub.

---

## 📜 **Giấy Phép**
📄 Dự án được cấp phép theo **MIT License** – Sử dụng tự do nhưng vui lòng giữ bản quyền.

🚀 **Chúc bạn code vui vẻ!** 😎  