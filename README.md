# SmartPark - Smart Parking Management System

A full-stack web application for managing parking slots, bookings, payments, and ratings. Built with **Spring Boot 3.3**, **Java 17**, and **Thymeleaf** with a modern responsive UI.

---

## 🎯 Features

### 👤 User Features
- **Registration & Email Verification** – Sign up with OTP-based email verification
- **Login/Logout** – Secure authentication with localStorage session management
- **Browse Parking Slots** – View all available slots in a clean grid layout
- **Book Slots** – Reserve parking by slot ID with start/end times
- **Payment** – Complete booking payment via booking ID and amount
- **Rate Experiences** – Submit 5-star ratings with optional comments on completed bookings
- **Profile Management** – Update name, contact, and password
- **Booking History** – View all bookings with status tracking (PENDING → APPROVED → COMPLETED)

### 🏢 Owner Features
- **Add Parking Slots** – Create slots with location and hourly price
- **Manage Slots** – Edit location/price, delete slots, track availability
- **Booking Requests** – Approve or reject booking requests from users
- **Analytics Dashboard** – View total bookings and revenue metrics
- **Slot Editing** – Modify slot details via inline edit interface

### 🔒 Security
- Email OTP verification for registration
- Stateless Spring Security configuration
- LocalStorage-based session management
- Password encryption for user authentication

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.3, Spring Data JPA, Spring Security |
| **Frontend** | Thymeleaf, Vanilla JavaScript (ES6), HTML5, CSS3 |
| **Database** | H2 (in-memory, embedded) |
| **Email** | JavaMailSender (Gmail SMTP) |
| **Build** | Maven 3.9+ |
| **Runtime** | Java 17+ |

---

## 📋 Prerequisites

- **Java 17+** (OpenJDK or Eclipse Adoptium)
- **Maven 3.9+** or Maven Wrapper (`mvnw`)
- **Gmail Account** (for OTP emails via SMTP)

### Gmail Setup (Required for Email OTP)
1. Enable 2-Factor Authentication on your Gmail account
2. Generate an **App Password** (16-char code) for third-party access
3. Update `application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-char-app-password
   ```

---

## 🚀 Quick Start

### 1. Clone & Navigate
```bash
cd c:\Users\ELCOT\Desktop\smart-parking-complete2
```

### 2. Configure Email (Optional but Recommended)
Edit `src/main/resources/application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.from=your-email@gmail.com
```

### 3. Build & Run
```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Or using installed Maven
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/smart-parking-0.0.1-SNAPSHOT.jar
```

### 4. Access Application
- **URL:** `http://localhost:8080`
- **Login Page:** `http://localhost:8080/login`
- **Register:** `http://localhost:8080/register`

---

## 📱 Application Pages

| Page | URL | Role | Purpose |
|------|-----|------|---------|
| **Register** | `/register` | Both | Sign up with OTP verification |
| **Login** | `/login` | Both | Sign in to account |
| **OTP Verify** | `/otp-verify` | Both | Email verification (standalone) |
| **Parking List** | `/parking-list` | Public | Browse all available slots |
| **User Dashboard** | `/user-dashboard` | User | Profile, bookings, browse slots |
| **Booking Form** | `/booking` | User | Create new booking |
| **Payment** | `/payment` | User | Pay for approved bookings |
| **Rating** | `/rating` | User | Rate completed bookings |
| **Owner Dashboard** | `/owner-dashboard` | Owner | Manage slots, bookings, analytics |

---

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/register           - Register new user
POST   /api/auth/login              - Login user
POST   /api/auth/send-otp           - Send OTP to email
POST   /api/auth/verify-otp         - Verify OTP code
```

### User
```
GET    /api/user/available-slots    - Get all available parking slots
POST   /api/user/book-slot          - Create new booking
GET    /api/user/bookings           - Get user's bookings
PUT    /api/user/profile            - Update profile (name, contact, password)
POST   /api/user/rating             - Submit rating for booking
PUT    /api/bookings/{id}/cancel    - Cancel booking
```

### Owner
```
POST   /api/owner/add-slot          - Create new parking slot
GET    /api/owner/my-slots          - Get owner's slots
PUT    /api/owner/slot/{id}         - Update slot details
DELETE /api/owner/slot/{id}         - Delete slot
GET    /api/owner/bookings          - Get bookings for owner's slots
PUT    /api/owner/bookings/{id}/status  - Approve/Reject booking
GET    /api/owner/analytics         - Get revenue & booking stats
```

---

## 📊 Booking Lifecycle

```
1. PENDING      → User creates booking, awaits owner approval
2. APPROVED     → Owner approves, user pays
3. COMPLETED    → Payment processed, user can rate
4. REJECTED     → Owner rejects (terminates flow)
5. CANCELLED    → User cancels (if PENDING or APPROVED)
```

---

## 📁 Project Structure

```
smart-parking-complete2/
├── src/
│   ├── main/
│   │   ├── java/com/smartparking/
│   │   │   ├── SmartParkingApplication.java
│   │   │   ├── config/
│   │   │   │   ├── MailConfig.java          (JavaMailSender bean)
│   │   │   │   └── SecurityConfig.java      (Spring Security setup)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java      (Register, Login, OTP)
│   │   │   │   ├── UserController.java      (Bookings, Profile, Rating)
│   │   │   │   ├── OwnerController.java     (Slots, Analytics)
│   │   │   │   ├── BookingController.java   (Booking operations)
│   │   │   │   ├── PaymentController.java   (Payments)
│   │   │   │   └── RatingController.java    (Ratings)
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── BookingService.java
│   │   │   │   ├── ParkingService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── RatingService.java
│   │   │   │   └── OTPService.java          (OTP generation & email)
│   │   │   ├── repository/                  (JPA Repositories)
│   │   │   ├── model/                       (JPA Entities)
│   │   │   └── dto/                         (Request/Response DTOs)
│   │   ├── resources/
│   │   │   ├── application.properties       (Config)
│   │   │   ├── application-mysql.properties (MySQL profile)
│   │   │   ├── templates/                   (Thymeleaf HTML)
│   │   │   │   ├── register.html
│   │   │   │   ├── login.html
│   │   │   │   ├── otp-verify.html
│   │   │   │   ├── user-dashboard.html
│   │   │   │   ├── owner-dashboard.html
│   │   │   │   ├── parking-list.html
│   │   │   │   ├── booking.html
│   │   │   │   ├── payment.html
│   │   │   │   └── rating.html
│   │   │   └── static/
│   │   │       ├── css/style.css            (Global styling)
│   │   │       └── js/
│   │   │           ├── main.js              (Core app logic)
│   │   │           └── payment.js           (Payment handling)
│   │   └── test/

├── pom.xml                                  (Maven dependencies)
└── README.md                                (This file)
```

---

## 🔧 Configuration

### Email (Gmail SMTP)
```properties
# src/main/resources/application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
spring.mail.from=your-email@gmail.com
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=30000
```

### Database
- **Default:** H2 in-memory (auto-creates on startup)
- **To use MySQL:** Update `application.properties` with MySQL credentials and set `spring.profiles.active=mysql`

### OTP Settings
```properties
otp.expiry.minutes=5               # OTP valid for 5 minutes
logging.level.com.smartparking=DEBUG
```

---

## 🧪 Testing the App

### Test User Flow
1. **Register:** Go to `/register` → enter details → verify OTP
2. **Login:** Use registered email/password
3. **Browse Slots:** Visit `/parking-list` to see available parking
4. **Book:** Click "Book Now" → enter slot ID, times → reserve
5. **Payment:** Complete payment with booking ID
6. **Rate:** Submit 5-star rating and feedback

### Test Owner Flow
1. **Register:** Role = OWNER
2. **Add Slots:** Owner Dashboard → add location & price
3. **Approve Bookings:** Accept/reject booking requests
4. **View Analytics:** Check total bookings & revenue

---

## 🐛 Troubleshooting

### Emails Not Arriving
- **Check SMTP settings** in `application.properties`
- **Gmail:** Verify App Password (not regular password)
- **Firewall/VPN:** May block SMTP port 587
- **Console Output:** Look for email sending logs with `[DEBUG]` tag

### Port 8080 Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### H2 Database Errors
- Database auto-initializes; if issues occur, clear `target/` and rebuild:
  ```bash
  mvn clean package
  ```

### Login Not Working
1. Ensure user registered and email verified
2. Check credentials are correct (case-sensitive email)
3. Verify localStorage is enabled in browser

### Slots Not Showing
- Owner must add slots first via Owner Dashboard
- Check if slot status is "available"
- Refresh page or check browser console for errors

---

## 📝 Demo Credentials

After initial setup, you can test with:

| Email | Password | Role |
|-------|----------|------|
| user@test.com | password123 | USER |
| owner@test.com | password123 | OWNER |

*(Register new accounts via `/register` page)*

---

## 📞 Support & Notes

- **OTP Console:** If email fails, check IntelliJ/terminal console for OTP code
- **CORS:** Currently allows all origins (disable in production)
- **Authentication:** Uses stateless JWT-like approach with localStorage
- **Database:** H2 resets on each restart (add `spring.h2.console.enabled=true` for H2 console access)

---

## 📜 License

This project is provided as-is for educational and commercial use.

---

## 🎓 Project by
**SmartPark Development Team** | March 2026
