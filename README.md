# University Library Management System

## Project Overview
این سیستم مدیریت کتابخانه دانشگاه یک سیستم تحت کامند لاین است که بدون ذخیره‌سازی داده در پایگاه داده یا فایل، تمام عملیات را در حافظه انجام می‌دهد.

## User Types and Features

### 1. دانشجو (Student)
**فرآیند:**
1. ثبت نام در سیستم با نام کاربری و کلمه عبور
2. ورود به سیستم
3. جستجوی کتاب بر اساس ترکیبی از عنوان، نویسنده و یا سال نشر
4. مشاهده وضعیت امانت کتاب (موجود هست یا خیر)
5. ثبت درخواست امانت کتاب با بازه زمانی (شروع و پایان)
6. مشاهده تاریخچه امانات خود

### 2. کاربر مهمان (Guest)
**فرآیند:**
1. مشاهده تعداد کل دانشجویان ثبت‌نام شده
2. جستجوی کتاب صرفاً بر اساس نام کتاب
3. مشاهده اطلاعات آماری شامل:
   - تعداد کل دانشجویان
   - تعداد کل کتاب‌ها
   - تعداد کل امانت‌ها
   - تعداد کتاب‌های فعلی در امانت

### 3. کارمند کتابخانه (Library Employee)
**فرآیند:**
1. ورود به سیستم (ثبت نام توسط مدیر انجام می‌شود)
2. تغییر رمز عبور
3. ثبت اطلاعات کتاب جدید
4. جستجو و ویرایش اطلاعات کتاب
5. بررسی و تایید درخواست امانت (برای همان روز یا روز قبل)
6. مشاهده گزارش تاریخچه امانات یک دانشجو با آمار:
   - تعداد کل امانات
   - تعداد کتاب‌های تحویل داده نشده
   - تعداد امانت‌های با تاخیر
7. فعال/غیرفعال کردن دانشجو
8. ثبت برگشت کتاب و زمان دریافت

### 4. مدیر سیستم (Manager)
**فرآیند:**
1. تعریف کارمند کتابخانه با نام کاربری و رمز عبور
2. مشاهده عملکرد کارمند:
   - تعداد کتاب‌های ثبت شده
   - تعداد کل کتاب‌های به امانت داده شده
   - تعداد کل کتاب‌های تحویل گرفته شده
3. مشاهده آمار امانات:
   - تعداد درخواست‌های امانت
   - تعداد کل کتاب‌های به امانت داده شده
   - میانگین تعداد روزهای امانت
4. مشاهده آمار دانشجویان:
   - تعداد کل دانشجویان
   - تعداد فعال/غیرفعال
   - لیست 10 دانشجوی با بیشترین تاخیر در تحویل

## Files Structure

- `Main.java` - فایل اصلی با رابط کاربری کامند لاین و منوها
- `Book.java` - کلاس کتاب با اطلاعات عنوان، نویسنده، سال نشر، ISBN و وضعیت
- `Student.java` - کلاس دانشجو با اطلاعات نام، ID، نام کاربری، رمز عبور و وضعیت فعال بودن
- `Employee.java` - کلاس کارمند با اطلاعات نام کاربری، رمز عبور و آمار عملکرد
- `Loan.java` - کلاس امانت با اطلاعات درخواست، تایید و برگشت کتاب
- `BookManager.java` - مدیریت کتاب‌ها و جستجو
- `StudentManager.java` - مدیریت دانشجویان
- `EmployeeManager.java` - مدیریت کارمندان
- `LoanManager.java` - مدیریت درخواست‌ها و امانت‌ها
- `LibrarySystem.java` - کلاس اصلی سیستم
- `MenuHandler.java` - مدیریت منوهای قدیمی (اختیاری)

## How to Run

```bash
# Compile all Java files
javac *.java

# Run the main application
java Main
```

## Default Manager Login
**Password:** `admin`

## Features Implementation

### Student Features
- ✓ Registration with username and password
- ✓ Login to system  
- ✓ Search books by title, author, and/or year
- ✓ View book availability status
- ✓ Request book loan with start and end dates
- ✓ View personal loan history

### Guest Features
- ✓ View total registered students count
- ✓ Search books by title only
- ✓ View statistics (students, books, loans, current loans)

### Employee Features  
- ✓ Login (registered by manager)
- ✓ Change password
- ✓ Register new books
- ✓ Search and edit book information
- ✓ Review and approve loan requests (today or yesterday)
- ✓ View student loan history with statistics
- ✓ Activate/deactivate students
- ✓ Record book returns with timestamp

### Manager Features
- ✓ Define library employees
- ✓ View employee performance statistics
- ✓ View loan statistics (requests, approved, average days)
- ✓ View student statistics with top 10 delayed returns

## Data Storage
تمام داده‌ها در حافظه (Memory) ذخیره می‌شوند و با بستن برنامه از بین می‌روند. هیچ فایل یا پایگاه داده استفاده نشده است.

