<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="iZludeClone Logo" width="120" />
  <h1>iZludeClone (Virtual Engine App Cloner)</h1>
  <p>Aplikasi Kloning (App Cloner) Modern menggunakan teknologi <b>Virtual OS / I/O Redirect</b> (tanpa akses Root) khusus untuk perangkat Android Modern.</p>
</div>

---

## 🌟 Fitur Utama
- **Isolasi Penuh (Sandbox)**: Aplikasi yang dikloning berjalan dalam ruang memori yang benar-benar terpisah dari sistem utama, berkat Virtual Engine BlackBox.
- **Data & Akun Terpisah**: Memungkinkan Anda untuk login ke dua akun (WhatsApp, Instagram, Game) yang berbeda secara bersamaan tanpa saling mengganggu.
- **Tanpa Root & Tanpa Work Profile**: Mengakali sistem vendor ketat (seperti ColorOS/MIUI) dengan virtualisasi murni, sehingga tidak memerlukan izin *Device Admin* atau *Work Profile* yang berisiko.
- **Material UI Design**: Antarmuka modern yang bersih, dilengkapi *Shimmer Loading* dan animasi transisi yang mulus.

## 🛠 Teknologi yang Digunakan
- **Bahasa**: Kotlin (100%)
- **Arsitektur**: Modern Android Architecture
- **Virtualisasi**: [BlackBox](https://github.com/FBlackBox/BlackBox) (Engine Inti)
- **Desain UI**: Material Components, ViewPager2, Shimmer Effect

---

## 💖 Ucapan Terima Kasih (Credits)
iZludeClone tidak akan terwujud tanpa dedikasi komunitas *Open-Source*, khususnya bagi pengembangan mesin virtual Android (Virtual Engine).

Penghargaan tertinggi dan kredit khusus diberikan kepada:
*   **[ALEX5402/NewBlackbox](https://github.com/ALEX5402/NewBlackbox)**: Untuk *fork* dan perbaikan luar biasa pada *engine* BlackBox sehingga kompatibel dengan versi Android terbaru (Android 12-14), serta menyediakan `Bcore-release.aar` yang siap pakai.
*   **[FBlackBox/BlackBox](https://github.com/FBlackBox/BlackBox)**: Proyek mesin virtual orisinal yang menjadi tulang punggung dari aplikasi ini.

## 📝 Lisensi
Proyek ini mengadopsi struktur *Virtual Engine* dari proyek BlackBox asli. Silakan merujuk pada lisensi perangkat lunak terbuka (Apache License 2.0 / GPL) dari *repository* BlackBox untuk penggunaan ulang mesin kloning.

## 🚀 Cara Menjalankan (Build)
1. *Clone* repository ini ke komputer Anda.
2. Buka proyek ini menggunakan **Android Studio**.
3. Pastikan Gradle melakukan sinkronisasi dengan sukses (diperlukan `Bcore-release.aar` di dalam folder `app/libs`).
4. Jalankan aplikasi (Run `app`) ke emulator atau perangkat Android fisik Anda.
