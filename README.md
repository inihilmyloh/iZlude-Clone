<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="iZludeClone Logo" width="120" />
  <h1>iZludeClone</h1>
  <p><b>Advanced Android App Virtualization & Cloning Engine</b></p>
  <p>Run multiple accounts simultaneously in an isolated sandbox environment without root or work profile restrictions.</p>
</div>

---

## 🌟 Overview

**iZludeClone** is a modern Android application that leverages **Virtual OS / I/O Redirect** technology to clone and run applications in a completely isolated sandbox environment. Unlike traditional cloning methods that rely on the Android Work Profile (which are often heavily restricted by device manufacturers like OPPO, Vivo, or Xiaomi), iZludeClone intercepts filesystem and system calls to create a self-contained virtual environment entirely within its own application directory.

## 🚀 Key Features

*   **🛡️ True Virtualization (Sandbox)**
    Cloned applications run in a completely isolated memory space. The Virtual Engine intercepts system calls and redirects storage paths (I/O hook) so that the cloned app operates independently of the host system.
*   **👥 Independent Data & Accounts**
    Seamlessly log into multiple accounts (e.g., WhatsApp, Instagram, Games) at the same time. The cloned application's data is safely stored inside iZludeClone's hidden directory, preventing any conflict with the original app.
*   **🔓 No Root or Work Profile Required**
    Bypass strict vendor OS limitations (such as ColorOS, MIUI, FuntouchOS). By utilizing pure application-level virtualization, iZludeClone does not require `Device Admin` privileges or dangerous Root permissions.
*   **✨ Modern Material UI**
    A sleek, responsive user interface built with Kotlin, featuring Material Components, ViewPager2, and Shimmer Loading effects for a premium user experience.

## 🛠️ Technology Stack

*   **Language**: Kotlin (100%)
*   **Architecture**: Modern Android Architecture
*   **Core Virtualization Engine**: BlackBox Framework (`Bcore-release.aar`)
*   **UI Components**: Material Design, Shimmer Effect

## 🚀 How to Build & Run

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/inihilmyloh/Ice-Clone.git
    ```
2.  **Open in Android Studio**
    Open the project using the latest version of Android Studio.
3.  **Sync Gradle**
    Ensure that Gradle syncs successfully. The virtualization engine (`Bcore-release.aar`) is already integrated directly into the `app/libs` directory.
4.  **Run the Application**
    Deploy the app to your emulator or physical Android device.

---

## 💖 Credits & Acknowledgments

iZludeClone would not be possible without the incredible dedication of the open-source community, particularly the developers pioneering Android Virtual Engines.

Special thanks and highest credits go to:

*   🏆 **[ALEX5402 / NewBlackbox](https://github.com/ALEX5402/NewBlackbox)**
    For their outstanding work in forking, maintaining, and fixing the BlackBox engine to ensure compatibility with modern Android versions (Android 12-14), and for providing the pre-compiled `Bcore-release.aar` that powers this app.
*   🏆 **[FBlackBox / BlackBox](https://github.com/FBlackBox/BlackBox)**
    For creating the original, groundbreaking open-source virtual engine architecture that serves as the foundation for this project.

## 📝 License

This project adopts the Virtual Engine architecture from the original BlackBox project. Please refer to the open-source licenses (Apache License 2.0 / GPL) from the BlackBox repository regarding the reuse of the cloning engine.
