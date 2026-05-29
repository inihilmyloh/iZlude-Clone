# DualSpace - Android App Cloner

Aplikasi Android untuk mengkloning/menduplikasi aplikasi lain (seperti Dual Space).

## Fitur

- 🔄 **Kloning Aplikasi** - Duplikasi aplikasi yang terinstall untuk menjalankan dua akun sekaligus
- 🔍 **Pencarian** - Cari aplikasi yang ingin dikloning dengan mudah
- 📱 **Manajemen Kloning** - Buka dan hapus kloning dengan mudah
- 🎨 **UI Modern** - Desain dark mode dengan animasi smooth
- 🔒 **Work Profile** - Menggunakan Android Work Profile untuk isolasi aplikasi

## Teknologi

- **Bahasa**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **UI**: Material Design 3
- **Arsitektur**: MVVM Pattern

## Cara Kerja

DualSpace menggunakan fitur **Android Managed Profile (Work Profile)** untuk membuat profil terpisah di perangkat. Pendekatan ini sama dengan yang digunakan oleh aplikasi seperti:
- **Shelter** (Open Source)
- **Island** by Oasis Feng

### Alur Kerja:
1. Setup Work Profile (profil kerja) di perangkat
2. Pilih aplikasi yang ingin dikloning
3. Aplikasi di-install di Work Profile
4. Jalankan dua instance dari aplikasi yang sama

## Build & Run

### Prasyarat
- Android Studio Arctic Fox atau lebih baru
- JDK 8+
- Android SDK 34

### Langkah Build
```bash
# Clone repository
git clone <repo-url>

# Buka di Android Studio
# Atau build via command line:
./gradlew assembleDebug
```

### Install ke device
```bash
./gradlew installDebug
```

## Struktur Proyek

```
app/src/main/
├── java/com/dualspace/clone/
│   ├── DualSpaceApp.kt          # Application class
│   ├── MainActivity.kt           # Splash screen
│   ├── core/
│   │   └── CloneManager.kt       # Core cloning logic
│   ├── model/
│   │   ├── AppInfo.kt            # App data model
│   │   └── ClonedApp.kt          # Cloned app model
│   ├── receiver/
│   │   └── DualSpaceDeviceAdminReceiver.kt
│   ├── service/
│   │   └── CloneService.kt       # Foreground service
│   ├── ui/
│   │   ├── HomeActivity.kt       # Main screen
│   │   ├── ClonedAppsFragment.kt  # Cloned apps list
│   │   ├── AppPickerFragment.kt   # App selection
│   │   ├── AppPickerActivity.kt   # Standalone picker
│   │   └── adapter/
│   │       ├── AppListAdapter.kt
│   │       └── ClonedAppAdapter.kt
│   └── util/
│       ├── AppUtils.kt           # App loading utilities
│       └── CloneStorage.kt       # Local storage
└── res/
    ├── layout/                    # XML layouts
    ├── drawable/                   # Icons & backgrounds
    ├── values/                     # Strings, colors, themes
    └── xml/                        # Device admin config
```

## Catatan Penting

⚠️ **Limitasi**:
- Beberapa perangkat mungkin tidak mendukung Work Profile
- Membutuhkan izin Device Admin untuk fitur penuh
- Aplikasi sistem tidak dapat dikloning

## Lisensi

MIT License
