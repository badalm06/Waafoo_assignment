# 💧 Waafoo – RO Purifier Service App
This project is an Android application created as part of an assignment for an Android Developer role. Waafoo helps users to track RO water purifier maintenance, view purifier health/status, and book technician service visits. The app is built using Kotlin, follows best practices with clean code, separation of concerns, and uses Kotlin XML for UI design. Core architecture uses Firebase as backend, and adheres to simple MVVM principles.

---

## 📱 Screenshots:

<img width="200" alt="Screenshot_20251112_152453" src="https://github.com/user-attachments/assets/e8224ff8-0d61-4137-bbcc-6c6ecbb91642" /> &nbsp;&nbsp;&nbsp;
<img width="200" alt="Screenshot_20251112_152504" src="https://github.com/user-attachments/assets/429853e6-02d7-450e-848b-0a12f0bec846" /> &nbsp;&nbsp;&nbsp;
<img width="200" alt="Screenshot_20251112_152520" src="https://github.com/user-attachments/assets/9f487c5b-d4a4-461d-a268-5e2631a4d3f2" /> &nbsp;&nbsp;&nbsp;
<img width="200" alt="Screenshot_20251112_152652" src="https://github.com/user-attachments/assets/83f5cc1e-dc8d-4c90-b526-9ccd714557e5" />

---

## ✨ Features
- User authentication with Firebase (Signup/Login/Logout with email/password).

- Dashboard showing:

  - Current purifier model/type

  - Last and next service date

  - Color-coded service status (healthy, due soon, overdue)

- Book Service: Form for technician visit booking, confirmation dialog on successful request.

- Quick actions: Book service, check water health (placeholder), view service history (placeholder).

- User info and all app data stored in Firebase Realtime Database.

- Modern UI with Material Design components and responsive layouts.

---

## 🔧 Libraries & Tools Used
- Kotlin for core development

- Firebase Authentication (email/password Auth)

- Firebase Realtime Database for cloud data

- ViewBinding for view management

- ConstraintLayout, CardView, Material Components for UI/UX

- Standard AndroidX libraries for navigation and core activity management

---

## 🧱 Project Structure
- Activities (Java/Kotlin)

  - LoginActivity: Handles user login using Firebase

  - SignupActivity: New user registration, collects purifier details

  - MainActivity: Dashboard screen with status, dates, quick actions

  - ServiceBookingActivity: Book a technician, form data sent to backend

- Resources

  - Layouts: activity_login.xml, activity_signup.xml, activity_main.xml, activity_service_booking.xml

  - Drawables: buttonshape.xml, edittextshape.xml (rounded backgrounds)

  - Values: colors.xml for theme

  - Font: abyssinica_sil.ttf for headers
  
---

## 🚀 Getting Started

### 🧰 Prerequisites

- Android Studio Hedgehog or later  
- Kotlin 1.9+  
- Gradle 8+  
- Min SDK 24+

### 🔧 Installation

1. **Clone the repository**
    ```bash
    git clone https://github.com/badalm06/Waafoo_assignment.git
    ```

2. **Open in Android Studio**

3. **Sync Gradle**  
   Dependencies will auto-resolve including:
   - Firebase Auth
   - Room

4. **Run the app**  
   On emulator or physical device.


---

## 💬 Contact & Support

For any queries or feedback, feel free to connect:

📧 Email: [badalsh908@gmail.com](mailto:badalsh908@gmail.com)  
🐙 GitHub: [github.com/badalm06](https://github.com/badalm06)

---

