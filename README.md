# Reading Story Web - Nền tảng Đọc Truyện Trực Tuyến

Ứng dụng web cho phép người dùng đọc, quản lý và tương tác với truyện tranh/tiểu thuyết trực tuyến. Dự án được xây dựng với Spring Boot 3.3.5, PostgreSQL, Redis, và triển khai bằng Docker với kiến trúc phân tán.

## 📋 Mục lục

- [Giới thiệu Dự án](#giới-thiệu-dự-án)
- [Chi tiết Thiết kế Hệ thống](#chi-tiết-thiết-kế-hệ-thống)
- [DevOps & Docker](#devops--docker)
- [Hướng dẫn Cài đặt](#hướng-dẫn-cài-đặt)
- [Lưu ý Khi Lập Trình](#lưu-ý-khi-lập-trình)

---

## 🎯 Giới thiệu Dự án

### Chức năng chính:
- 📚 **Quản lý Truyện**: Tạo, chỉnh sửa, xóa truyện
- 📖 **Quản lý Chương**: Tổ chức nội dung truyện thành các chương
- 👥 **Hệ thống Người dùng**: Đăng ký, đăng nhập, quản lý hồ sơ
- 💬 **Bình luận & Phản ứng**: Tương tác cộng đồng
- 🏪 **Shop & Inventory**: Hệ thống mua sắm trong ứng dụng
- 📊 **Lịch sử Đọc**: Theo dõi tiến độ đọc
- 🔐 **Xác thực JWT**: OAuth2 Resource Server
- ☁️ **Lưu trữ Media**: Tích hợp Cloudinary

### Công nghệ sử dụng:
- **Backend**: Java 17, Spring Boot 3.3.5, Spring Security, Spring Data JPA
- **Cơ sở dữ liệu**: PostgreSQL 14 (Cluster với Replication)
- **Cache & Distributed Lock**: Redis 6.2
- **Load Balancing**: Nginx, PgPool-II
- **Containerization**: Docker & Docker Compose

---

## 🏗️ Chi tiết Thiết kế Hệ thống

### 1. Kiến trúc tổng quát

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React/Next.js)                │
│                      (Port 3000)                            │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/HTTPS
┌─────────────────────────▼────────────────────────────────────┐
│                    Nginx Load Balancer                       │
│                      (Port 80/443)                           │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┴────────────────┐
        │                                 │
┌───────▼────────┐         ┌──────────────▼────────┐
│  Spring Boot   │         │   Spring Boot Apps    │
│  Application   │         │   (Multiple Instances)│
│  (Port 8080)   │         │   (Horizontal Scale)  │
└───────┬────────┘         └──────────┬─────────────┘
        │                             │
        └──────────────┬──────────────┘
                       │ JDBC Connection Pooling
        ┌──────────────▼──────────────┐
        │      PgPool-II (Port 5432)  │
        │   Load Balancing & Failover │
        └────────────────┬────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
    ┌───▼────┐      ┌───▼────┐      ┌───▼────┐
    │ PostgreSQL│ PostgreSQL  │ PostgreSQL  │
    │  Master  │  Slave 1    │  Slave 2    │
    │(Primary) │ (Read-only) │ (Read-only) │
    └──────────┘  └──────────┘  └──────────┘
    
    Replication Manager (Repmgr) cho failover tự động

        ┌─────────────────────────┐
        │  Redis Cluster (6379)   │
        │ - Session Cache         │
        │ - Distributed Lock      │
        │ - Data Cache            │
        └─────────────────────────┘
```

### 2. Thành phần chính

#### 2.1 PostgreSQL Cluster (Primary-Replica)
- **Mô hình Replication**: Master → Slave1, Slave2 (Streaming Replication)
- **Quản lý Failover**: Repmgr tự động chuyển từ node chết sang Slave
- **Đặc điểm**:
  - Write: Chỉ vào Master
  - Read: Có thể từ Master hoặc Slaves (với repmgr, có thể load balance)
  - Database: `app_db`
  - User: `app_user` (mật khẩu: `app_password`)
  - Replication user: `repmgr` (mật khẩu: `repmgrpass`)

**Kiến trúc dữ liệu**:
```
app_db/
  ├── Users (user_entity)
  ├── Stories (story_entity)
  ├── Chapters (chapter_entity)
  ├── Comments (comment_entity)
  ├── Reactions (reaction_entity)
  ├── Reading History (reading_history_entity)
  ├── Inventory Items (inventory_entity)
  ├── Point History (point_history_entity)
  └── Roles & Permissions
```

#### 2.2 Redis
- **Vị trí**: Lưu trữ cache tạm thời, session, distributed locks
- **Cấu hình**:
  - Host: `redis:6379` (trong Docker network)
  - Persistent: `redis_data` volume
  - ACL user: `admin` (mật khẩu: `AdminPassword`)
  
**Cách sử dụng**:
```yaml
spring:
  redis:
    host: redis        # Service name trong docker-compose
    port: 6379
    connect-timeout: 2000ms
    lettuce:
      pool:
        max-active: 4  # Connection pool size
        max-idle: 4
        min-idle: 0
```

#### 2.3 PgPool-II
- **Chức năng**: 
  - Load balancing cho đọc từ Slaves
  - Connection pooling
  - Automatic failover
  - Health check các PostgreSQL nodes

- **Cấu hình**:
  - Port: 5432 (Application kết nối vào PgPool, không trực tiếp PostgreSQL)
  - Health check: 5 giây
  - Stream Replication Check: 10 giây
  - Failover: Tự động khi backend error

#### 2.4 Nginx Load Balancer
- **Chức năng**: 
  - Reverse proxy cho application
  - Load balancing nếu có multiple app instances
  - SSL termination (có thể setup)
  
- **Cấu hình**:
  - Port: 80/443
  - Config file: `environment/nginx.conf`
  - Upstream: `app:8080`

### 3. Network Architecture

**Docker Networks**:
- `frontend`: Kết nối Nginx ↔ Frontend
- `backend`: Kết nối App ↔ Database ↔ Redis ↔ PgPool

**Tách biệt network** đảm bảo:
- Database không accessible từ frontend
- Chỉ Nginx expose ports để client truy cập

---

## 🐳 DevOps & Docker

### 1. Docker Compose Services

```yaml
services:
  app:
    - Spring Boot application
    - Build từ Dockerfile (2-stage build)
    - Port: 8080 (internal), expose qua Nginx
    - Network: frontend + backend
    
  load_balancer (Nginx):
    - Reverse proxy + load balancing
    - Port: 80 (HTTP)
    - Network: frontend
    
  redis:
    - Redis 6.2
    - Port: 6379
    - Persistent data: redis_data volume
    - Network: backend
    
  pgpool:
    - Connection pooler for PostgreSQL
    - Port: 5432
    - Network: backend
    
  postgresql-master:
    - Primary database
    - Replication streaming
    - Network: backend
    
  postgresql-slave1, postgresql-slave2:
    - Read replicas
    - Automatic replication from master
    - Network: backend
```

### 2. Dockerfile (Multi-stage Build)

```dockerfile
# Stage 1: Build
FROM maven:3.9.14-amazoncorretto-17-debian AS build
# - Compile source code
# - Package JAR file
# Output: /app/target/*.jar

# Stage 2: Runtime
FROM amazoncorretto:17.0.18
# - Copy JAR từ stage 1
# - Run application
# Result: Smaller image (~400MB vs 1GB+)
```

**Lợi ích**:
- ✅ Image size nhỏ (chỉ cần JDK runtime, không cần Maven)
- ✅ Security (dependencies không lộ ra)
- ✅ Build cache optimization

### 3. Docker Volumes

| Volume | Dùng cho | Persistence |
|--------|----------|-------------|
| `pg_master_data` | PostgreSQL Master data | ✅ Permanent |
| `pg_slave1_data` | PostgreSQL Slave 1 data | ✅ Permanent |
| `pg_slave2_data` | PostgreSQL Slave 2 data | ✅ Permanent |
| `redis_data` | Redis data | ✅ Permanent |

### 4. Environment Variables

```bash
# PostgreSQL
POSTGRESURL=jdbc:postgresql://pgpool:5432/app_db?prepareThreshold=0
POSTGRESUSER=app_user
POSTGRESPASSWORD=app_password

# Redis
REDIS_HOST=redis
REDISPORT=6379

# Application
PORT=8080
FRONTEND_URL=http://localhost:3000
```

### 5. Cấu hình Health Check

**PgPool Health Check**:
- Period: 5 giây
- Nếu node down → tự động failover

**Redis Connection Pool**:
- Max active: 4 connections
- Timeout: 2000ms
- Auto-reconnect: Enabled

**Database Replication**:
- SR Check Period: 10 giây
- Detected lag: Automatic failover nếu lag quá lớn

---

## 🚀 Hướng dẫn Cài đặt

### Yêu cầu Hệ thống

- Docker Desktop (version 20.10+)
- Docker Compose (version 2.10+)
- Git
- Terminal/Command Prompt

### Bước 1: Clone Repository

```bash
# Clone project
git clone https://github.com/your-repo/reading-story-web.git
cd reading-story-web

# Verify project structure
ls -la
# Bạn sẽ thấy: pom.xml, Dockerfile, docker-compose.yml (trong environment/), etc.
```

### Bước 2: Cấu hình Environment

```bash
# Di chuyển vào thư mục environment
cd environment

# (Optional) Tùy chỉnh docker-compose.yml nếu cần:
# - Thay đổi ports
# - Database passwords
# - Redis passwords
```

### Bước 3: Build và Start Docker Containers

```bash
# Từ thư mục environment/
docker-compose up -d

# Hoặc build images trước:
docker-compose up -d --build

# Kiểm tra status containers
docker-compose ps

# Expected output:
# NAME                 STATUS              PORTS
# app                  Up (healthy)        8080->8080/tcp
# load_balancer        Up                  80->80/tcp
# redis                Up                  6379->6379/tcp
# pgpool               Up                  5432->5432/tcp
# postgresql-master    Up                  
# postgresql-slave1    Up                  
# postgresql-slave2    Up
```

### Bước 4: Kiểm tra Application

```bash
# Check logs
docker-compose logs -f app

# Test API endpoint
curl http://localhost:80/api/health
# hoặc http://localhost:8080/api/health

# Test database connection
docker-compose exec pgpool psql -U app_user -d app_db -c "SELECT 1"

# Test Redis connection
docker-compose exec redis redis-cli ping
# Response: PONG
```

### Bước 5: Dừng và Cleanup

```bash
# Stop containers (giữ lại data)
docker-compose down

# Stop và xóa volumes (xóa tất cả data)
docker-compose down -v

# View container logs (sau khi down)
docker-compose logs app | tail -50
```

### Useful Docker Commands

```bash
# View real-time logs
docker-compose logs -f app

# Access application shell
docker-compose exec app /bin/bash

# Connect to PostgreSQL
docker-compose exec pgpool psql -U app_user -d app_db

# Connect to Redis
docker-compose exec redis redis-cli

# Rebuild specific service
docker-compose up -d --build app

# View resource usage
docker stats

# Remove unused images/volumes
docker system prune -a --volumes
```

---

## 📝 Lưu ý Khi Lập Trình

### 1. Distributed Lock với Redis cho Cron Jobs

**⚠️ Vấn đề**: Khi có multiple instances của application chạy (horizontal scaling), các scheduled jobs sẽ chạy trên tất cả instances → data bị duplicate.

**✅ Giải pháp**: Sử dụng Redis Distributed Lock

#### Ví dụ: ChapterJobScheduler (Hiện tại - CÓ VẤN ĐỀ)

```java
@Service
@RequiredArgsConstructor
public class ChapterJobScheduler {
    private final StringRedisTemplate stringRedisTemplate;
    private final ChapterRepository chapterRepository;

    @Scheduled(cron = "0 */5 * * * ?")  // Mỗi 5 phút
    public void syncAndResetChapterViews() {
        Set<String> keys = stringRedisTemplate.keys("chapter::*");
        // ⚠️ PROBLEM: Nếu có 3 app instances, job này chạy 3 lần
        // → Dữ liệu được cập nhật 3 lần, views bị ghi đè
        
        if (keys != null) {
            for (String key : keys) {
                String viewCountStr = stringRedisTemplate.opsForValue().get(key);
                if (viewCountStr != null) {
                    String chapterId = key.split("::")[1];
                    updateChapterViewsCount(chapterId, viewCountStr);
                    stringRedisTemplate.opsForValue().set(key, "0");
                }
            }
        }
    }
}
```

#### Giải pháp: Thêm Distributed Lock

```java
@Service
@RequiredArgsConstructor
public class ChapterJobScheduler {
    private final StringRedisTemplate stringRedisTemplate;
    private final ChapterRepository chapterRepository;
    
    private static final String LOCK_KEY = "job:chapter:sync:lock";
    private static final String LOCK_VALUE = "locked";
    private static final long LOCK_TIMEOUT_SECONDS = 300; // 5 phút

    @Scheduled(cron = "0 */5 * * * ?")
    public void syncAndResetChapterViews() {
        // Thử lấy lock
        Boolean lockAcquired = stringRedisTemplate.opsForValue()
            .setIfAbsent(LOCK_KEY, LOCK_VALUE, 
                Duration.ofSeconds(LOCK_TIMEOUT_SECONDS));
        
        if (lockAcquired == null || !lockAcquired) {
            // Lock đã bị chiếm, skip job này
            log.warn("Không thể acquire lock, job đã chạy trên instance khác");
            return;
        }

        try {
            // Chỉ 1 instance sẽ thực thi code này
            Set<String> keys = stringRedisTemplate.keys("chapter::*");
            
            if (keys != null) {
                for (String key : keys) {
                    String viewCountStr = stringRedisTemplate.opsForValue().get(key);
                    if (viewCountStr != null) {
                        String chapterId = key.split("::")[1];
                        updateChapterViewsCount(chapterId, viewCountStr);
                        stringRedisTemplate.opsForValue().set(key, "0");
                    }
                }
            }
        } finally {
            // Giải phóng lock
            stringRedisTemplate.delete(LOCK_KEY);
        }
    }

    private void updateChapterViewsCount(String chapterId, String viewCountStr) {
        int viewCount = Integer.parseInt(viewCountStr);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        
        if (chapter != null) {
            chapter.setViewCount(viewCount);
            chapterRepository.save(chapter);
        }
    }
}
```

#### Hoặc: Dùng Annotation Helper (Advanced)

```java
@Service
@RequiredArgsConstructor
public class ChapterJobScheduler {
    private final StringRedisTemplate stringRedisTemplate;
    private final ChapterRepository chapterRepository;

    @Scheduled(cron = "0 */5 * * * ?")
    @RedisDistributedLock(key = "job:chapter:sync", timeout = 300)
    public void syncAndResetChapterViews() {
        // Chỉ 1 instance thực thi, không cần manual lock handling
        Set<String> keys = stringRedisTemplate.keys("chapter::*");
        // ... rest of logic
    }
}
```

Tạo annotation helper:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisDistributedLock {
    String key();
    long timeout() default 300;
}

@Aspect
@Component
@RequiredArgsConstructor
public class RedisDistributedLockAspect {
    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, 
                         RedisDistributedLock lock) throws Throwable {
        Boolean acquired = stringRedisTemplate.opsForValue()
            .setIfAbsent(lock.key(), "locked", 
                Duration.ofSeconds(lock.timeout()));
        
        if (acquired == null || !acquired) {
            return null; // Skip execution
        }

        try {
            return joinPoint.proceed();
        } finally {
            stringRedisTemplate.delete(lock.key());
        }
    }
}
```

### 2. Redis Connection Best Practices

```yaml
spring:
  redis:
    host: redis
    port: 6379
    timeout: 10000
    lettuce:
      pool:
        max-active: 4      # Số connection tối đa
        max-idle: 4        # Số connection idle tối đa
        min-idle: 0        # Số connection tối thiểu
        timeout: 2000ms    # Timeout lấy connection từ pool
    connect-timeout: 2000ms # Timeout khi kết nối
```

**Lưu ý**:
- Pool size phải đủ lớn cho số threads của application
- Quá nhỏ → timeout, quá lớn → lãng phí memory
- Typical: 4-10 cho application size trung bình

### 3. Database Transaction & Replication

**Lưu ý khi làm việc với PostgreSQL cluster**:

```java
// ❌ SAI: Read từ master (không cần)
@Service
public class StoryService {
    @Transactional(readOnly = true)
    public Story getStory(String id) {
        // Đây sẽ đọc từ master (write instance)
        // Tốn tài nguyên của master
        return storyRepository.findById(id).orElse(null);
    }
}

// ✅ ĐÚNG: Cấu hình read replica explicit (nếu cần)
@Service
public class StoryService {
    @Transactional(readOnly = true)
    // PgPool sẽ tự động route READ queries tới Slaves
    public Story getStory(String id) {
        return storyRepository.findById(id).orElse(null);
    }
}
```

**PgPool tự động xử lý**:
- `SELECT` queries → Slaves
- `INSERT/UPDATE/DELETE` → Master
- Không cần cấu hình thêm ở application level

### 4. Caching Strategy

```java
@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final StringRedisTemplate redisTemplate;

    public Chapter getChapter(String id) {
        // 1. Kiểm tra Redis cache
        String cacheKey = "chapter::" + id;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.readValue(cached, Chapter.class);
        }

        // 2. Không có trong cache, lấy từ database
        Chapter chapter = chapterRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Chapter not found"));

        // 3. Lưu vào Redis cache (5 phút)
        redisTemplate.opsForValue().set(cacheKey, 
            objectMapper.writeValueAsString(chapter),
            Duration.ofMinutes(5));

        return chapter;
    }

    public void updateChapter(String id, ChapterDTO dto) {
        Chapter chapter = chapterRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Chapter not found"));
        
        chapter.setTitle(dto.getTitle());
        chapter.setContent(dto.getContent());
        chapterRepository.save(chapter);

        // 4. Xóa cache khi update
        String cacheKey = "chapter::" + id;
        redisTemplate.delete(cacheKey);
    }
}
```

### 5. Error Handling cho Distributed System

```java
@Service
@RequiredArgsConstructor
public class ResilientService {
    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public Data fetchData(String id) {
        try {
            // Try primary data source
            return externalApiCall(id);
        } catch (Exception e) {
            log.warn("Primary source failed, trying cache", e);
            
            // Fallback to cache
            String cached = redisTemplate.opsForValue()
                .get("data::" + id);
            if (cached != null) {
                return objectMapper.readValue(cached, Data.class);
            }
            
            // Fallback to database
            log.warn("Cache miss, reading from database");
            return databaseCall(id);
        }
    }
}
```

### 6. Monitoring & Logging

```java
@Configuration
public class LoggingConfig {
    // Log slow queries (> 1 second)
    // Log Redis operations
    // Monitor connection pool usage
}

// application.yml
logging:
  level:
    org.springframework.data.redis: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    
spring:
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

### 7. Các Job Schedulers hiện có

| Scheduler | Cron | Chức năng | Cần Lock? |
|-----------|------|----------|----------|
| `ChapterJobScheduler` | `0 */5 * * * ?` | Sync view counts | ✅ **CẦN** |
| `InventoryJobScheduler` | TBD | Sync inventory | ✅ **CẦN** |
| `LevelJobScheduler` | TBD | Update user levels | ✅ **CẦN** |
| `StoryJobScheduler` | TBD | Process stories | ✅ **CẦN** |

**ACTION**: Áp dụng distributed lock cho tất cả schedulers

---

## 🔧 Troubleshooting

### PostgreSQL Connection Issues

```bash
# Check PgPool status
docker-compose logs pgpool | grep -i "error\|failed"

# Test direct connection to master
docker-compose exec postgresql-master psql -U app_user -d app_db -c "SELECT 1"

# Check replication lag
docker-compose exec postgresql-master psql -U repmgr -d repmgr -c "SELECT * FROM pg_stat_replication;"
```

### Redis Connection Issues

```bash
# Check Redis logs
docker-compose logs redis

# Test Redis
docker-compose exec redis redis-cli ping

# Monitor Redis commands
docker-compose exec redis redis-cli monitor
```

### Application Startup Issues

```bash
# View app logs
docker-compose logs app --tail 100

# Rebuild app
docker-compose up -d --build app

# Check environment variables
docker-compose exec app env | grep -E "POSTGRES|REDIS|PORT"
```

---

### Useful Links
- Redis CLI: `docker-compose exec redis redis-cli`
- PostgreSQL Console: `docker-compose exec pgpool psql -U app_user -d app_db`

### Project Structure

```
reading-story-web/
├── Dockerfile                 # Build image
├── pom.xml                    # Maven dependencies
├── environment/
│   ├── docker-compose.yml    # Docker services
│   ├── nginx.conf            # Nginx config
│   └── redis-users.acl       # Redis ACL
├── src/
│   ├── main/
│   │   ├── java/com/viettran/reading_story_web/
│   │   │   ├── config/       # Spring config
│   │   │   ├── controller/   # REST endpoints
│   │   │   ├── service/      # Business logic
│   │   │   ├── entity/       # JPA entities
│   │   │   ├── repository/   # Data access
│   │   │   ├── scheduler/    # Cron jobs
│   │   │   ├── dto/          # Data transfer objects
│   │   │   ├── mapper/       # Entity ↔ DTO mapping
│   │   │   └── exception/    # Custom exceptions
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── target/                    # Build output
└── README.md                 # This file
```

---

## 📞 Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra logs: `docker-compose logs [service-name]`
2. Verify services: `docker-compose ps`
3. Test connectivity: `docker-compose exec [service] [command]`
4. Reset everything: `docker-compose down -v && docker-compose up -d`

