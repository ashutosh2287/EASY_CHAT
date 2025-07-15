# 💬 EasyChat - Real-Time Android Chat Application

**EasyChat** is a modern real-time chat app built using **Kotlin** and **Firebase**. It allows users to authenticate via OTP, chat one-on-one, search for users, and manage profile data. The app is designed with performance, simplicity, and mobile responsiveness in mind — a perfect blend of Firebase's power and Material UI design.

**Developer**: Ashutosh Anand  
**Development Period**: 2024–2025  

---

## ✨ Features

- 🔐 **Phone Number Authentication** with Firebase OTP
- 🧑‍💬 **User Profiles**: Set username and profile photo
- 💬 **1-to-1 Chat**: Real-time messaging using Firestore
- 🕒 **Recent Chats List**: Optimized with embedded user details
- 🔍 **Search Users** by username
- 🖼️ **Profile Picture Viewer** with full zoom-in layout
- 📤 **Offline Message Support** with Firestore cache
- 🔔 **Push Notifications** (via FCM - optional)
- 🧠 **Clean Architecture**: MVVM-inspired structure with modular design

---

## 🛠️ Tech Stack

### Android
- **Kotlin** - Modern Android development language
- **Jetpack Libraries** - Lifecycle, ViewModel, etc.
- **Material Design** - UI/UX design system
- **RecyclerView** - Efficient list rendering

### Firebase
- **FirebaseAuth** - Phone OTP authentication
- **Cloud Firestore** - Real-time database
- **Firebase Storage** - For profile pictures
- **Firebase Cloud Messaging** - Push notifications

### Tools
- **Glide** - Image loading and caching
- **Gradle** - Dependency management
- **Android Studio** - Development environment

---

## 📂 Project Structure

EasyChat/
├── activities/
│ ├── ChatActivity.kt
│ ├── LoginPhoneNumberActivity.kt
│ ├── LoginOtpActivity.kt
│ ├── LoginUsernameActivity.kt
│ └── SearchUserActivity.kt
├── fragments/
│ ├── ChatFragment.kt
│ └── ProfileFragment.kt
├── adapters/
│ ├── ChatRecyclerAdapter.kt
│ ├── RecentChatRecyclerAdapter.kt
│ └── SearchUserRecyclerAdapter.kt
├── models/
│ ├── UserModel.kt
│ ├── ChatroomModel.kt
│ └── ChatMessageModel.kt
├── utils/
│ ├── AndroidUtil.kt
│ └── FirebaseUtil.kt
├── layout/
│ ├── *.xml (all activity + recycler row UIs)
└── MainActivity.kt


---

## 🧭 App Flow

1. **Splash Screen** → Auth check
2. **Login** → Phone number + OTP → Username + Profile
3. **MainActivity** → Contains Chat & Profile Fragments
4. **SearchUserActivity** → Start chat with any user
5. **ChatActivity** → Real-time conversation

---

## 🔐 Firebase Security Rules (Sample)

```

---

## 🧭 App Flow

1. **Splash Screen** → Auth check
2. **Login** → Phone number + OTP → Username + Profile
3. **MainActivity** → Contains Chat & Profile Fragments
4. **SearchUserActivity** → Start chat with any user
5. **ChatActivity** → Real-time conversation

---

## 🔐 Firebase Security Rules (Sample)

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }

    match /chatrooms/{chatroomId} {
      allow read, write: if request.auth.uid in resource.data.userIds;
    }

    match /messages/{messageId} {
      allow read, write: if request.auth.uid != null;
    }
  }
}


 Key Activities and Components
ChatActivity – Real-time message screen

SearchUserActivity – Search and start chats

RecentChatRecyclerAdapter – Shows latest chats efficiently

FirebaseUtil.kt – Centralized Firestore/Auth handling

ChatroomModel – Stores chatroomId, last message, and cached user info

💡 Future Improvements
✅ Image sharing in chat

✅ Group chat support

⏳ Seen/delivered indicators

⏳ Delete messages

⏳ Typing indicator

⏳ Message encryption

👨‍💻 Developer
Ashutosh Anand

LinkedIn: https://www.linkedin.com/in/ashutosh-anand-1651841b6/

GitHub: https://github.com/ashutosh2287

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🙏 Acknowledgments
Firebase for real-time services

Android Developers community for support

Inspiration from WhatsApp and Signal for UI/UX patterns
