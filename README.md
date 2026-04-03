# 📚 VieTruyen – Backend API
## Nền tảng đọc truyện tranh online với xử lý bài toán thực tế

---

## I. Mô tả dự án

### 📖 Reading Story Web Backend API
**VieTruyen** là nền tảng backend xây dựng cho website đọc truyện tranh online, tập trung vào giải quyết các bài toán thực tế như:

- **Phân quyền người dùng** (RBAC: Admin, Author, User) với JWT & Token Whitelist
- **Quản lý nội dung** có lịch phát hành và quy trình kiểm duyệt: tác giả đăng truyện -> admin review -> publish truyện
- **Hệ thống comment phân cấp** tối ưu dung lượng đọc (Nested Set Model)
- **Caching** với Redis giảm tải database
- **Hệ thống shop** và **xếp hạng người dùng** theo điểm & level
- **Quản lý tệp hình ảnh** trên cloud (Cloudinary)


## II. Tech Stack

### Backend
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security**

### Devops & Tool
- **Nginx**
- **AWS (EC2, S3)**
- **Docker**

### Database & Caching
- **MySQL 8.0+**
- **Redis**

## III. Problem Solutions

### 🎯 1. Nested Set Model – Comment System (Read-Heavy Optimization)

#### Bài toán đặt ra:
- Comment theo **hình thức cây** (tree structure) với **depth không giới hạn**
- Tần suất **đọc comment rất cao** (mỗi lần mở chapter)
- Tần suất **viết comment ít hơn** so với đọc
- Cần **truy vấn nhanh** toàn bộ cây comment

#### Giải pháp: Nested Set Model

**Nguyên tắc hoạt động**:

Mỗi node (comment) được gán 2 giá trị: `left_val` và `right_val`. Cây được "duyệt tuyến tính" từ trái sang phải:

```
                    Comment A (1, 12)
                   /         \
            Comment B         Comment C (9, 10)
            (2, 7)              (8, 11)
           /   |   \
          D    E    F
        (3,4)(5,6)(7,8)
```

**Các thao tác và độ phức tạp**:

| Thao tác | SQL | Độ phức tạp |
|---------|-----|-----------|
| **Lấy toàn bộ cây** | `SELECT * WHERE left >= L AND right <= R ORDER BY left` | **O(1)** – 1 query |
| **Thêm comment** | Update `left/right` của các node cần shift, insert node mới | **O(N)** – N là số node |
| **Xóa subtree** | Delete và update các node phía sau | **O(N)** |
| **Lấy parent** | `SELECT * WHERE left < L AND right > R ORDER BY (right-left) LIMIT 1` | **O(N)** nhưng nhanh |

**Lợi ích**:
- **1 query** để lấy toàn bộ cây (vs. N+1 queries với recursive)
- Không cần `@OneToMany` relationships – giảm memory
- Dễ **render tree** ở client theo left/right order
- Xóa subtree cũng chỉ vài queries
- **Hiệu năng**: ~10x nhanh hơn so với recursive queries

**Nhược điểm**:
- Thêm/xóa comment phức tạp hơn (phải shift values)
- Update khi có sự thay đổi structure

---

### 🎯 2. Redis Caching – Optimize Database Load

**Bài toán**: Top stories, ranking, hot content được access **hằng ngày hàng nghìn lần** → tải quá cao lên database

**Giải pháp**: Lưu cache vào Redis với TTL (Time-To-Live)

**Cache Models**:

**a) StoryCache** (TTL: 1 giờ)
```java
@RedisHash(value = "StoryCache", timeToLive = 3600)
public class StoryCache {
    @Id
    int id;
    String name;
    String authorName;
    int viewCount;
    double rate;
    int commentCount;
    boolean hot;
    // ... other fields
}
```

**b) Ranking Cache** (Revalidate hàng ngày)
```java
// Dùng Redis Sorted Set để lưu ranking
// Key: "ranking:daily"
// Score: points hoặc level
// Member: userId
```

**Chiến lược caching**:

1. **Cache-Aside Pattern**:
```java
public List<StoryResponse> getHotStories() {
    // 1. Kiểm tra Redis
    List<StoryCache> cached = storyCacheRepository.findHotStories();
    if (!cached.isEmpty()) {
        return toResponses(cached);
    }
    
    // 2. Nếu miss → query database
    List<Story> stories = storyRepository.findHotStories();
    
    // 3. Lưu vào Redis
    stories.forEach(s -> storyCacheRepository.save(toStoryCache(s)));
    
    return toResponses(stories);
}
```

2. **Scheduled Revalidation** (Refresh cache).
```java
@Component
public class StoryJobScheduler {
    @Scheduled(fixedRate = 3600000) // Every 1 hour
    public void refreshHotStoriesCache() {
        List<Story> hotStories = storyRepository.findHotStories();
        hotStories.forEach(s -> storyCacheRepository.save(toStoryCache(s)));
    }
}
```

3. **Invalidate khi write**:
```java
@Transactional
public StoryResponse updateStory(StoryUpdateRequest req) {
    Story story = storyRepository.save(updated);
    
    // Invalidate cache
    storyCacheRepository.deleteById(story.getId());
    
    return toStoryResponse(story);
}
```

**Hiệu suất**:
- **Trước**: 1 query DB = ~50ms
- **Sau**: 1 query Redis = ~1-5ms

---

### 🎯 3. RBAC (Role-Based Access Control) + JWT Token Whitelist

**3 vai trò chính**:
- **Admin**: Quản lý hệ thống, duyệt nội dung
- **Author**: Đăng truyện/chapter
- **User**: Đọc và tương tác

**Token Whitelist**:
- Mỗi token hợp lệ được lưu trong **DisabledToken table**
- Khi đăng xuất → thêm token vào whitelist
- Mỗi request kiểm tra token có trong whitelist không


## 📡 API Documentation

- **Postman Collection**: [Reading-Request.postman_collection.json](Reading-Request.postman_collection.json)
- **Base URL**: `http://localhost:8080/api/v1`

---


---

## IV. Core Functionalities

| Tính năng | Đặc điểm |
|---------|----------|
| **Tìm kiếm truyện** | Lọc theo tên, thể loại, số chapter, độ hot, rating |
| **Lịch sử đọc** | Lưu local + lưu DB (phân đoạn theo độ dài) |
| **Bán item** | Mua khung avatar, items bằng coin, đăng nhập để nhận coin |
| **Điểm & Level** | Tính dựa trên chapter đọc, có bảng xếp hạng |
| **Comment phân cấp** | Nested Set Model, support emoji, like/dislike |
| **Theo dõi truyện** | Follow/unfollow, nhận thông báo chapter mới |

## V. Cài đặt dự án

### 🔧 Yêu cầu môi trường

- **JDK**: 17+ (khuyến nghị: JDK 21)
- **Maven**: 3.8+ 
- **MySQL**: 8.0+
- **Redis**: 7.0+ (Local hoặc Docker)

### 📥 Bước cài đặt

#### 1. Clone repository
```bash
git clone https://github.com/helloVietTran/reading-comic.git
cd reading-comic/backend
```

#### 2. Cài đặt dependencies
```bash
mvn clean install
```

#### 3. Cấu hình kết nối database và API key(application.yml)

#### 4. Chạy ứng dụng
```bash
mvn spring-boot:run

```

**Server chạy tại**: `http://localhost:8080/api/v1`
---

## 6. Project Structure

```
backend/
├── src/main/java/com/viettran/reading_story_web/
│   ├── config/              # Cấu hình Spring (Security, Redis, JPA)
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic
│   ├── repository/
│   │   ├── jpa/            # JPA Repository (MySQL)
│   │   ├── redis/          # Redis Repository
│   │   └── httpClient/     # HTTP client cho external API
│   ├── entity/
│   │   ├── mysql/          # Entity models (Story, Comment, User...)
│   │   ├── redis/          # Redis cache models
│   │   └── base/           # BaseEntity với timestamp
│   ├── dto/
│   │   ├── request/        # DTO cho incoming requests
│   │   └── response/       # DTO cho API responses
│   ├── mapper/             # MapStruct mappers (DTO ↔ Entity)
│   ├── exception/          # Exception handling
│   ├── enums/              # Enum constants
│   ├── scheduler/          # Scheduled tasks (@Scheduled)
│   └── utils/              # Utility classes
│
├── src/main/resources/
│   ├── application.yml     # Cấu hình ứng dụng
│   └── templates/          # Thymeleaf templates (email...)
│
├── pom.xml                 # Maven dependencies
├── Dockerfile              # Docker configuration
└── README.md               # Tài liệu dự án
```


