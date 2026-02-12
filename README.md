# Website đọc truyện online

> **Đồ án tốt nghiệp** - Thành viên thực hiện: Trần Việt Anh

## 📚 I. Giới thiệu
Dự án website đọc truyện sinh ra để đáp ứng nhu cầu giải trí ngày càng tăng của người dùng. Dự án được thiết kế theo kiến trúc master-slave, sử dụng nginx làm load balancer và pg pool làm SQL balancer

Dự án được xây dựng đáp ứng các chức năng cốt lõi với kiến trúc cho phép scale khi số lượng người đọc tăng cao.

1. 📝 Tài liệu đặc tả và trình bày solution: [Docs here](https://docs.google.com/spreadsheets/d/1rAm5o3OZ-tzAVuw5nkkkmWQYOj4ntg29-iN0OXCSagA/edit?gid=0#gid=0)

2. 📝 Tài liệu thiết kế hệ thống: [Docs here](https://docs.google.com/spreadsheets/d/13syJTJZKzJfNnn7L2Mt_TXI5rLFMfkJUXQjZLTbDqqU/edit?gid=0#gid=0)

## 🔥 II. Mô tả một số bài toán nổi bật đã xử lý

### 1. Xác thực và phân quyền người dùng (RBAC + Token Whitelist)

Hệ thống áp dụng mô hình **RBAC (Role-Based Access Control)** với 3 actor chính:

- **Admin**: quản lý hệ thống, duyệt nội dung
- **Author**: đăng và quản lý truyện/chapter của mình
- **User**: đọc và tương tác nội dung

Quy trình xác thực sử dụng **JWT**, kết hợp với cơ chế **Token Whitelist** để tăng cường bảo mật.  
Mỗi token hợp lệ phải tồn tại trong whitelist, cho phép hệ thống:

- Chủ động **thu hồi token** khi người dùng đăng xuất
- Kiểm soát phiên đăng nhập trên nhiều thiết bị
- Giảm rủi ro khi token bị lộ

Phân quyền được kiểm soát ở cả **route-level** và **business-level**, đảm bảo mỗi actor chỉ có thể truy cập đúng phạm vi chức năng được cấp.

---

###  2. Quy trình đăng truyện có lịch phát hành và kiểm duyệt

Hệ thống hỗ trợ **Author đăng truyện/chapter kèm lịch phát hành (schedule publish)** theo quy trình sau:

1. Author tạo truyện/chapter và thiết lập thời điểm phát hành mong muốn
2. Nội dung được chuyển sang trạng thái **chờ duyệt**
3. **Admin thực hiện kiểm duyệt nội dung** trước khi cho phép phát hành

Để đảm bảo luồng phát hành không bị gián đoạn:
- Nếu **quá thời điểm phát hành đã định mà Admin chưa duyệt**, hệ thống sẽ **tự động gửi thông báo nhắc nhở đến Admin**
- Sau khi Admin duyệt thành công, **thời gian phát hành được tự động cộng thêm 1 ngày**, đảm bảo nội dung vẫn được hiển thị hợp lệ và không bị “miss lịch”

Giải pháp này giúp:
- Tách biệt rõ trách nhiệm Author – Admin
- Tránh tình trạng nội dung bị treo do chậm duyệt
- Giữ trải nghiệm nhất quán cho người đọc

---

### 3. Bài toán phân phối nội dung theo chiến lược Explore – Exploit

Hệ thống triển khai bài toán **đề xuất truyện theo chiến lược Explore – Exploit** nhằm cân bằng giữa việc **giữ chân người dùng** và **tăng khả năng khám phá nội dung mới**.

Giải pháp tập trung vào việc **phân phối xen kẽ** giữa:
- **Exploit**: các truyện đang có hiệu suất cao (top view, trending, nhiều tương tác) được ưu tiên để đảm bảo trải nhiệm ổn định
- **Explore**: các truyện mới hoặc ít lượt xem sẽ được phân phối thêm với tỷ lệ hợp lý để tăng tính khám phá


---


## III. Công nghệ sử dụng

**Frontend:** React.js, TaiwindCSS, Shacdn UI, Redux Toolkit, React Query

**Backend:**

1. Service xử lý nghiệp vụ chính: Spring Boot, Spring Security, Spring Data, Spring Thymeleaf

2. Service phụ trợ (Notification): Node.js, Express.js

3. Tool phụ trợ (Crawl dữ liệu, xử lý bài toán explore-exploit, hệ thống gợi ý): 

**Design Pattern:** Singleton, Builder Pattern

**Database:** PostgreSQL, MongoDB, Redis

**Tools:** Docker

## IV. Một số hình ảnh demo dự án

<table>
  <tr>
    <td><img src="./demo/pic_15.png" width="500"/></td>
    <td><img src="./demo/pic_14.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_13.png" width="500"/></td>
    <td><img src="./demo/pic_12.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_11.png" width="500"/></td>
    <td><img src="./demo/pic_10.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_9.png" width="500"/></td>
    <td><img src="./demo/pic_8.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_7.png" width="500"/></td>
    <td><img src="./demo/pic_6.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_5.png" width="500"/></td>
    <td><img src="./demo/pic_4.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_3.png" width="500"/></td>
    <td><img src="./demo/pic_2.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="./demo/pic_1.png" width="500"/></td>
  </tr>
</table>

## ⚙️ V. Hướng dẫn cài đặt

### Yêu cầu hệ thống
- Docker 4.55.0
- Java JDK 17+
- Maven 3.7+
- Node.js v22+
  
### Các bước chạy dự án
1. **Clone dự án:**
```
   git clone https://github.com/helloVietTran/graduate-project
```
2. **Thiết lập môi trường phát triển:**
```
   cd graduate-project
   cd environment
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