# Personal Attendance Tracker System

MVP full-stack cho mô hình:

- Frontend PWA: Vercel, domain `attendance.ducit.io.vn`
- Backend Spring Boot: Railway, domain `api-attendance.ducit.io.vn`
- Database PostgreSQL: Railway

## Structure

- `frontend`: React + Vite PWA
- `backend`: Spring Boot REST API + JPA
- `Documents`: tài liệu yêu cầu gốc

## Local Run

Backend:

```powershell
cd backend
mvn spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

App mặc định gọi API ở `http://localhost:8080`.

## Deploy Backend To Railway

Create Railway service from `backend`.

Set variables:

```text
PORT=8080
FRONTEND_ORIGIN=https://attendance.ducit.io.vn
DATABASE_URL=jdbc:postgresql://<host>:<port>/<database>
DATABASE_USERNAME=<postgres-user>
DATABASE_PASSWORD=<postgres-password>
DATABASE_DRIVER=org.postgresql.Driver
```

Build command:

```text
mvn -DskipTests package
```

Start command:

```text
java -jar target/personal-attendance-tracker-0.0.1-SNAPSHOT.jar
```

Docker option:

```text
Dockerfile path: backend/Dockerfile
```

If the deploy platform builds from inside the `backend` directory, use:

```text
Dockerfile path: Dockerfile
```

Sau khi deploy, gắn custom domain `api-attendance.ducit.io.vn` vào Railway service.

## Deploy Frontend To Vercel

Create Vercel project from `frontend`.

Set variable:

```text
VITE_API_BASE_URL=https://api-attendance.ducit.io.vn
```

Build command and output are already defined in `frontend/vercel.json`.

Sau khi deploy, gắn custom domain `attendance.ducit.io.vn` vào Vercel project.

## API Summary

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/me`
- `PUT /api/me`
- `GET /api/schedule`
- `PUT /api/schedule`
- `POST /api/attendance/check-in`
- `POST /api/attendance/check-out`
- `GET /api/attendance`
- `GET /api/attendance/dashboard`
- `GET /api/admin/dashboard`
- `GET /api/admin/users`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `PATCH /api/admin/users/{id}/enabled?value=true|false`
- `GET /api/admin/users/{id}/attendance`

Tài khoản đầu tiên đăng ký sẽ tự động là `ADMIN`; các tài khoản sau là `USER`.
