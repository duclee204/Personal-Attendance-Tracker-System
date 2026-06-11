# Personal Attendance Tracker System

MVP full-stack cho mô hình:

- Frontend PWA: Vercel, URL `https://fe-personal-attendance-tracker-syst.vercel.app`
- Backend Spring Boot: Render, URL `https://be-personal-attendance-tracker-system.onrender.com`
- Database PostgreSQL: Render

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

## Deploy Backend To Render

Create Render web service from `backend`.

Set variables:

```text
PORT=8080
FRONTEND_ORIGIN=https://fe-personal-attendance-tracker-syst.vercel.app
DATABASE_HOST=<render-postgres-host>
DATABASE_PORT=5432
DATABASE_NAME=personal_attendance
DATABASE_USERNAME=admin
DATABASE_PASSWORD=<postgres-password>
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

Backend hiện đang chạy tại `https://be-personal-attendance-tracker-system.onrender.com`.

## Deploy Frontend To Vercel

Create Vercel project from `frontend`.

Set variable:

```text
VITE_API_BASE_URL=https://be-personal-attendance-tracker-system.onrender.com
```

Build command and output are already defined in `frontend/vercel.json`.

Frontend hiện đang chạy tại `https://fe-personal-attendance-tracker-syst.vercel.app`.

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
