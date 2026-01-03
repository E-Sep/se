# گزارش دیباگ و اصلاح کد پروژه

## تاریخ: 2026-01-03

## خطاهای شناسایی شده و رفع شده:

### 1. خطای ساختار Package - LibrarySystem.java و MenuHandler.java
**خطا:** 
```
cannot find symbol: class MenuHandler
location: class system.LibrarySystem
```

**علت:**
- فایل‌های `LibrarySystem.java` و `MenuHandler.java` در package `system` بودند اما فایل‌های فیزیکی در root directory (`src/main/java/`) قرار داشتند
- در Java، ساختار دایرکتوری باید با package declaration مطابقت داشته باشد

**راه‌حل:**
- دایرکتوری `src/main/java/system/` ایجاد شد
- فایل `LibrarySystem.java` به `src/main/java/system/LibrarySystem.java` منتقل شد
- فایل `MenuHandler.java` به `src/main/java/system/MenuHandler.java` منتقل شد

**فایل‌های تغییر یافته:**
- `src/main/java/LibrarySystem.java` → `src/main/java/system/LibrarySystem.java`
- `src/main/java/MenuHandler.java` → `src/main/java/system/MenuHandler.java`

---

### 2. خطای متغیر تکراری در BorrowController.java
**خطا:**
```
variable req is already defined in method setup
```

**علت:**
- در متد `setup` از `BorrowController`، پارامتر lambda به نام `req` تعریف شده بود
- در داخل lambda نیز متغیر محلی با همان نام `req` تعریف شده بود

**راه‌حل:**
- پارامتر lambda از `req` به `request` تغییر نام داد (خط 78)
- متغیر محلی از `req` به `reqMap` تغییر نام داد (خط 89)
- متغیر `res` به `response` تغییر نام داد (خط 100)

**فایل تغییر یافته:**
- `src/main/java/api/BorrowController.java` (خطوط 78, 89, 100)

---

### 3. Import MenuHandler در Main.java
**مشکل:**
- `Main.java` از `MenuHandler` استفاده می‌کرد اما import نداشت

**راه‌حل:**
- `import system.MenuHandler;` به `Main.java` اضافه شد

**فایل تغییر یافته:**
- `src/main/java/Main.java` (خط 11)

---

## خلاصه تغییرات:

### فایل‌های منتقل شده:
1. ✅ `LibrarySystem.java` → `system/LibrarySystem.java`
2. ✅ `MenuHandler.java` → `system/MenuHandler.java`

### فایل‌های اصلاح شده:
1. ✅ `api/BorrowController.java` - تغییر نام متغیرها
2. ✅ `Main.java` - اضافه کردن import MenuHandler

### فایل‌های بدون تغییر:
- تمام فایل‌های API (AuthController, BookController, StudentController, StatsController, AdminController, RestApiServer, SeedData)
- تمام فایل‌های Manager (BookManager, EmployeeManager, LoanManager, StudentManager)
- تمام فایل‌های Model (Book, BorrowRequest, Employee, Loan, Student, LibraryStats, StudentReport)
- تمام فایل‌های Exception
- فایل‌های Service

---

## نتیجه نهایی:

✅ **کامپایل موفق:** `mvn compile` بدون خطا اجرا می‌شود
✅ **ساختار Package صحیح:** تمام کلاس‌ها در package مناسب خود قرار دارند
✅ **بدون خطای کامپایل:** هیچ خطای syntax یا semantic وجود ندارد
✅ **بدون warning مهم:** فقط یک warning درباره `--release 11` که غیرقابل توجه است

---

## دستورات تست:

```bash
# کامپایل پروژه
mvn clean compile

# اجرای REST API Server
mvn exec:java "-Dexec.mainClass=api.RestApiServer"

# یا با استفاده از اسکریپت
.\run-rest-api.ps1
```

---

## وضعیت نهایی:
🟢 **پروژه آماده تحویل است**

تمام خطاهای کامپایل رفع شده و پروژه بدون مشکل کامپایل می‌شود.

