# 🌱 Linkarium

**Linkarium** is an Android application developed with **Jetpack Compose** and **Kotlin**, aimed at offering an organized and visually appealing space to manage collections of ideas, links, or content related to personal projects. Its modular design and modern architecture allow for a fluid, adaptable, and easily scalable experience.

---

## 🚀 Main Features

* **State management with ViewModel and Flow**, ensuring reactive and efficient updates.
* **Modern interface in Jetpack Compose**, with fluid animations and reusable components.
* **Local persistence with Room**, supported by a data export and import system.

---

## 🧩 Architecture

The project follows the **MVVM (Model-View-ViewModel)** approach, complemented by Clean Architecture principles:

```
app/
 ├─ ui/                # Jetpack Compose screens and components
 ├─ domain/            # Use cases and business models
 ├─ data/              # Repositories, local and remote data sources
 ├─ di/                # Hilt configuration (Dependency Injection)
 └─ core/              # Base configurations and shared logic
```

---

## 🧠 Technologies Used

* **Kotlin** — Main language.
* **Jetpack Compose** — Declarative UI.
* **Room** — Local database.
* **Hilt** — Dependency Injection.
* **Paging 3** — Efficient large list handling.
* **Gson** — Data serialization and deserialization.
* **Coroutines + Flow** — Asynchronous and reactive programming.
* **PDFBox** — PDF generation.

> Check the libraries used in the [DEPENDENCIES](DEPENDENCIES.md) file or in [`libs.versions.toml`](gradle/libs.versions.toml).

---

## ⚙️ Project Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/andres2002002/Linkarium
   ```
2. Open the project in **Android Studio (Latest version)**.
3. Sync Gradle dependencies.
4. Run the application on an emulator or physical device.

---

## 🌤️ Export and Backup

Linkarium allows you to **export your data in PDF or JSON format**.

---

## 🧪 Testing and Development

* Critical functions are covered with **unit and integration tests**.
* **Mockito** is used for simulating dependencies.
* Views can be tested in isolation thanks to the modular architecture.

---

## 💬 Contributions

Contributions are welcome! If you have an idea or improvement, open an *issue* or submit a *pull request*.

---

## ✨ Author

**Developed by andres2002002**
Project in constant evolution as part of **Linkarium Development** 🌿

## License

Copyright (C) 2026 andres2002002

Linkarium is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published
by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

Linkarium is distributed under the GNU General Public License,
version 3 or later (GPL-3.0-or-later).

See [LICENSE](LICENSE) for the complete license text.

### Third-Party Software

Linkarium uses third-party open-source libraries. Their respective
licenses remain applicable to those components.

See [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md) for the
runtime dependencies used by Linkarium.

### Legal

- [Privacy Policy](PRIVACY.md)
- [Terms of Use](TERMS.md)
- [Third-Party Licenses](THIRD_PARTY_LICENSES.md)