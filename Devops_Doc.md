# Quá trình thực hành

Mục tiêu: deploy trên EC2 (AWS) hoặc VPS cloud, dùng Docker container, reverse proxy với Nginx.

## 1. Tạo image và đẩy lên Docker Hub

- Build image trên local:
```bash
  docker build -t trandanhviet192003040/reading-comic-app:1.0.0 .
```

- Đăng nhập Docker Hub:

```bash
docker login
```
- Push image:
```bash
docker push trandanhviet192003040/reading-comic-app:1.0.0
```

## 2. Chuẩn bị cloud server (EC2, DigitalOcean, Linode...)

- Tạo instance Linux (cấu hình 2vcpu, 2gb RAM, 13Gb SSD).
- Tạo Elastic IP gắn liên với EC2 để cố định IP mỗi lần EC2 reboot
- Mở port 80 và 443 ở security group (inbouce rules)

## 3. Cài Docker trên cloud

Ubuntu:
```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg lsb-release
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo usermod -aG docker $USER
newgrp docker
```

## 4. Pull image và chạy container service

- Pull image:

```bash
docker pull trandanhviet192003040/reading-comic-app:1.0.0
```

- Tạo network để các container giao tiếp:

```
docker network create reading-comic-network
```

- Chạy các container service cần thiết trong mạng `reading-comic-network`.Đối với Ubuntu, MacOS thay ký tự **`** bằng ký tự **/**
```
docker run -d `
  --name mysql-db `
  --network reading-comic-network `
  -p 3306:3306 `
  -e MYSQL_ROOT_PASSWORD=root `
  -e MYSQL_DATABASE=reading_story_web_db `
  -e MYSQL_USER=app_user `
  -e MYSQL_PASSWORD=app_pass `
  mysql:9.1.0

docker run -d `
  --name redis-db `
  --network reading-comic-network `
  -p 6379:6379 `
  redis:7.4.1
```

- Chạy app container với các biến môi trường phù hợp:

```
docker run -d `
  --name reading-comic-app `
  --network reading-comic-network `
  -p 8080:8080 `
  -e FRONTEND_URL="http://localhost:5173" `
  -e MYSQL_CONNECTION="jdbc:mysql://mysql-db:3306/reading_story_web_db" `
  -e MYSQL_USER="app_user" `
  -e MYSQL_PASS="app_pass" `
  -e REDIS_HOST="redis-db" `
  -e REDIS_PORT="6379" `
  reading-comic-app:1.0.0

```

- Chạy container app theo `profile` nếu có:

```bash

docker run -d --name reading-comic-app --restart unless-stopped -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MYSQL_HOST=mysql-db-host -e MYSQL_PORT=3306 -e MYSQL_DB=reading_story_web_db \
  -e MYSQL_USER=app_user -e MYSQL_PASS=app_pass \
  -e REDIS_HOST=redis-host -e REDIS_PORT=6379 \
  trandanhviet192003040/reading-comic-app:1.0.0
```

## 5. Cài Nginx và cấu hình reverse proxy

- Cài Nginx:
  ```bash
  sudo apt install -y nginx
  ```
- Tạo file config site, ví dụ `/etc/nginx/sites-available/reading-comic`:
  ```nginx
  server {
      listen 80;
      server_name site-cloud.xyz;

      root /var/www/react-app;
      index index.html;

      location / {
          try_files $uri /index.html;
      }

      location /api/ {
          proxy_pass http://localhost:8080;
          proxy_http_version 1.1;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
      }
  }
  ```

- Giải thích cấu hình:
  - `listen 80`: lắng nghe HTTP.
  - `server_name`: domain hoặc IP server.
  - `root` + `index`: phục vụ static FE (React) từ thư mục.
  - `location /`: route frontend SPA, fallback về `index.html` khi client-side routing.
  - `location /api/`: reverse proxy tới backend trên cùng máy `localhost:8080`.

- Kích hoạt site và reload Nginx:
  ```bash
  sudo ln -s /etc/nginx/sites-available/reading-comic /etc/nginx/sites-enabled/
  sudo nginx -t
  sudo systemctl reload nginx
  ```

- Kiểm tra cấu hình Nginx:
  - `sudo nginx -t`: kiểm tra syntax và config validity.
  - `sudo systemctl status nginx`: xem trạng thái.
  - `curl -I http://localhost`: kiểm tra HTTP response.

## 6. HTTPS (tuỳ chọn):

- Cài Certbot và cấu hình SSL:
  ```bash
  sudo apt install -y certbot python3-certbot-nginx
  sudo certbot --nginx -d site-cloud.xyz
  sudo systemctl reload nginx
  ```

## 7. Build FE và upload lên EC2

- Trên local repo FE:
  ```bash
  cd frontend
  npm install
  npm run build
  ```
- Upload thư mục `dist` (hoặc `build`) qua FileZilla/SFTP lên EC2 vào `/var/www/react-app`.
- Thiết lập quyền:
  ```bash
  sudo chown -R www-data:www-data /var/www/react-app
  sudo chmod -R 755 /var/www/react-app
  ```

## 8. Kiểm tra cuối cùng

- Mở `http://site-cloud.xyz`
- Kiểm tra api call `/api/` có được chuyển tới backend.
