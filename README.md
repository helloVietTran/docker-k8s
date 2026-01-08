# Website đọc truyện online

> **Đồ án tốt nghiệp** - Thành viên thực hiện: Trần Việt Anh

## Giới thiệu
Dự án website đọc truyện sinh ra để đáp ứng nhu cầu giải trí ngày càng tăng của người dùng. Dự án được thiết kế theo kiến trúc phân tán để chịu lỗi và dễ dàng scale cho hàng ngàn người dùng.

### 📝 Tài liệu yêu cầu bài toán và trình bày solution: [Docs here](https://docs.google.com/spreadsheets/d/1rAm5o3OZ-tzAVuw5nkkkmWQYOj4ntg29-iN0OXCSagA/edit?gid=0#gid=0)

---

## 🛠 Công nghệ sử dụng

**Frontend:** React.js, TaiwindCSS, Shacdn UI, Redux Toolkit, React Query

**Backend:** Spring Boot, Spring Security, Spring Data, Spring Thymeleaf, OpenCV

**Database:** PostgreSQL, MongoDB, Redis

**Devops:** Docker

**Design Pattern:** Singleton, Specification Pattern + Builder Pattern

## 🏗  Kiến trúc hệ thống

### 📝 Tài liệu thiết kế hệ thống: [Docs here](https://docs.google.com/spreadsheets/d/13syJTJZKzJfNnn7L2Mt_TXI5rLFMfkJUXQjZLTbDqqU/edit?gid=0#gid=0)

Hệ thống được thiết kế theo kiến trúc phân tán:
1. Nginx đóng vai trò Load balancer (phân phối tải theo thuật toán Round Robin tới các App Server)

2. Kiến trúc PostgreSQL Master–Replication
3. Caching tại Nginx và CloudFlare

## 📦 Hướng dẫn cài đặt

### Yêu cầu hệ thống
- Docker 4.55.0
- Java JDK 17+
- Maven 3.7+
- Node.js v22+
  
### Các bước chạy dự án
1. **Clone dự án:**
```bash
   git clone https://github.com/helloVietTran/graduate-project
```
2. **Thiết lập môi trường phát triển:**
```
   cd graduate-project
   docker compose up -d
```

3. **Run backend:**
  
```
   cd backend
   mvn clean install
   mvn spring-boot:run
```

4. **Run frontend:**

```
   cd frontend
   npm install
   npm run dev
```

