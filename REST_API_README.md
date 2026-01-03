# REST API Documentation

## راه‌اندازی

برای راه‌اندازی REST API سرور، از دستور زیر استفاده کنید:

```bash
java -cp target/classes:target/dependency/* api.RestApiServer
```

یا با استفاده از Maven:

```bash
mvn compile exec:java -Dexec.mainClass="api.RestApiServer"
```

سرور روی پورت **8081** اجرا می‌شود و API از آدرس زیر قابل دسترسی است:

```
http://localhost:8081/api
```

## احراز هویت

بیشتر API ها نیاز به احراز هویت دارند. پس از لاگین، یک token دریافت می‌کنید که باید در header همه درخواست‌ها ارسال شود:

```
Authorization: Bearer <token>
```

## لیست API ها

### 1. احراز هویت (Authentication)

#### ثبت‌نام دانشجو
- **POST** `/api/auth/register`
- Body:
```json
{
  "name": "نام دانشجو",
  "studentId": "401001",
  "username": "username",
  "password": "password"
}
```

#### ورود به سیستم
- **POST** `/api/auth/login`
- Body:
```json
{
  "username": "username",
  "password": "password",
  "userType": "student" // یا "employee" یا "manager"
}
```

#### تغییر رمزعبور (کارمند/مدیر)
- **POST** `/api/auth/change-password`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "oldPassword": "old",
  "newPassword": "new"
}
```

### 2. کتاب‌ها (Books)

#### دریافت لیست کتاب‌ها
- **GET** `/api/books?title=Java&author=Bloch&year=2020`
- Query Parameters: `title`, `author`, `year` (اختیاری)

#### دریافت جزئیات یک کتاب
- **GET** `/api/books/{isbn}`

#### ایجاد کتاب جدید (کارمند)
- **POST** `/api/books`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "title": "عنوان کتاب",
  "author": "نویسنده",
  "year": 2020,
  "isbn": "ISBN-0001"
}
```

#### به‌روزرسانی اطلاعات کتاب (کارمند)
- **PUT** `/api/books/{isbn}`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "title": "عنوان جدید",
  "author": "نویسنده جدید",
  "year": 2021
}
```

#### جستجوی پیشرفته کتاب
- **GET** `/api/books/search?title=Java&author=Bloch&year=2020`

### 3. امانت کتاب (Borrowing)

#### ثبت درخواست امانت (دانشجو)
- **POST** `/api/borrow/request`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "bookIsbn": "ISBN-0001",
  "startDate": "2024-01-01",
  "endDate": "2024-01-15"
}
```

#### مشاهده درخواست‌های در انتظار (کارمند)
- **GET** `/api/borrow/requests/pending`
- Headers: `Authorization: Bearer <token>`

#### تایید درخواست امانت (کارمند)
- **PUT** `/api/borrow/requests/{id}/approve`
- Headers: `Authorization: Bearer <token>`
- Note: `{id}` به صورت `username_isbn` است

#### رد درخواست امانت (کارمند)
- **PUT** `/api/borrow/requests/{id}/reject`
- Headers: `Authorization: Bearer <token>`

#### ثبت بازگرداندن کتاب (کارمند)
- **PUT** `/api/borrow/{id}/return`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "wasLate": false
}
```

### 4. مدیریت دانشجویان (Students)

#### دریافت پروفایل دانشجو
- **GET** `/api/students/{id}`
- Note: `{id}` می‌تواند username یا studentId باشد

#### فعال/غیرفعال کردن دانشجو (کارمند)
- **PUT** `/api/students/{id}/status`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "isActive": true
}
```

#### مشاهده تاریخچه امانت‌های دانشجو (کارمند)
- **GET** `/api/students/{id}/borrow-history`
- Headers: `Authorization: Bearer <token>`

### 5. گزارش‌ها و آمار (Reports & Statistics)

#### آمار خلاصه (عمومی)
- **GET** `/api/stats/summary`

#### آمار پیشرفته امانت‌ها (مدیر)
- **GET** `/api/stats/borrows`
- Headers: `Authorization: Bearer <token>` (مدیر)

#### گزارش عملکرد کارمند (مدیر)
- **GET** `/api/stats/employees/{username}/performance`
- Headers: `Authorization: Bearer <token>` (مدیر)

#### لیست دانشجویان با بیشترین تاخیر (مدیر)
- **GET** `/api/stats/top-delayed`
- Headers: `Authorization: Bearer <token>` (مدیر)

### 6. مدیریت کارکنان (Employees) - فقط مدیر

#### ایجاد حساب کارمند جدید
- **POST** `/api/admin/employees`
- Headers: `Authorization: Bearer <token>` (مدیر)
- Body:
```json
{
  "username": "emp1",
  "password": "1234"
}
```

#### لیست کارکنان
- **GET** `/api/admin/employees`
- Headers: `Authorization: Bearer <token>` (مدیر)

## مثال استفاده

### 1. ثبت‌نام و لاگین دانشجو

```bash
# ثبت‌نام
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"علی احمدی","studentId":"401001","username":"ali","password":"1111"}'

# لاگین
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ali","password":"1111"}'
```

### 2. دریافت لیست کتاب‌ها

```bash
curl http://localhost:8081/api/books
```

### 3. درخواست امانت کتاب (با token)

```bash
curl -X POST http://localhost:8081/api/borrow/request \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer token_1234567890_1234" \
  -d '{"bookIsbn":"ISBN-0001","startDate":"2024-01-01","endDate":"2024-01-15"}'
```

## نکات مهم

1. تمام پاسخ‌ها در قالب JSON هستند
2. برای API های نیازمند احراز هویت، باید token را در header ارسال کنید
3. برای ورود به عنوان مدیر، `userType` را `"manager"` و password را `"admin"` قرار دهید
4. تاریخ‌ها باید به فرمت ISO_LOCAL_DATE (`YYYY-MM-DD`) باشند




