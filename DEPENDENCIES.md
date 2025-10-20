# 📦 Dependencias del Proyecto

Este documento describe las librerías y plugins utilizados en el proyecto, junto con su propósito, versión y enlaces a documentación oficial.

---

## ⚙️ Plugins de compilación

* **Android Gradle Plugin**  
  ID: `com.android.application` — **v8.13.0**  
  👉 Plugin principal para compilar y empaquetar aplicaciones Android.  
  🔗 [Documentación](https://developer.android.com/build)

* **Kotlin Android**  
  ID: `org.jetbrains.kotlin.android` — **v2.2.20**  
  👉 Permite el uso de Kotlin en proyectos Android.  
  🔗 [Kotlin en Android](https://developer.android.com/kotlin)

* **Kotlin Compose**  
  ID: `org.jetbrains.kotlin.plugin.compose` — **v2.2.20**  
  👉 Habilita la integración de Jetpack Compose en proyectos Kotlin.  
  🔗 [Compose Compiler Plugin](https://developer.android.com/jetpack/compose/setup)

* **Kotlin Serialization**  
  ID: `org.jetbrains.kotlin.plugin.serialization` — **v2.2.20**  
  👉 Permite la serialización y deserialización eficiente de objetos Kotlin.  
  🔗 [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization)

* **Dagger Hilt**  
  ID: `com.google.dagger.hilt.android` — **v2.57.2**  
  👉 Plugin para configurar automáticamente Hilt en proyectos Android.  
  🔗 [Hilt Android](https://dagger.dev/hilt/)

* **Google KSP**  
  ID: `com.google.devtools.ksp` — **v2.2.20-2.0.3**  
  👉 Procesador de anotaciones moderno que reemplaza a KAPT.
  > La versión de KSP depende directamente de la versión de Kotlin.  
  🔗 [Tabla oficial de compatibilidad](https://github.com/google/ksp/releases)

---

## 🖥️ UI y Compose

* **Activity Compose**  
  `androidx.activity:activity-compose` — **v1.11.0**  
  👉 Permite integrar actividades con Jetpack Compose.  
  🔗 [Activity Compose](https://developer.android.com/jetpack/androidx/releases/activity)

* **Compose BOM**  
  `androidx.compose:compose-bom` — **v2025.10.00**  
  👉 Asegura la compatibilidad entre todos los módulos de Jetpack Compose.  
  🔗 [Compose BOM](https://developer.android.com/jetpack/compose/bom)

* **Compose UI**
  * `androidx.compose.ui:ui`
  * `androidx.compose.ui:ui-graphics`
  * `androidx.compose.ui:ui-tooling`
  * `androidx.compose.ui:ui-tooling-preview`  
    👉 Conjunto base de librerías para construir interfaces con Compose.  
    🔗 [Compose UI](https://developer.android.com/jetpack/compose)

* **Compose Material 3**  
  `androidx.compose.material3:material3`  
  👉 Implementación moderna de Material Design 3.  
  🔗 [Compose Material3](https://developer.android.com/jetpack/androidx/releases/compose-material3)

* **Material Icons Extended**  
  `androidx.compose.material:material-icons-extended`  
  👉 Conjunto adicional de íconos para Compose Material.  
  🔗 [Material Icons](https://developer.android.com/jetpack/compose/icons)

* **Material3 Window Size Class**  
  `androidx.compose.material3:material3-window-size-class`  
  👉 Permite adaptar el diseño según el tamaño de pantalla.  
  🔗 [Window Size Class](https://developer.android.com/jetpack/compose/layouts/adaptive)

* **Compose Foundation**  
  `androidx.compose.foundation:foundation`  
  👉 Componentes básicos de interfaz como listas, gestos y layouts.  
  🔗 [Compose Foundation](https://developer.android.com/jetpack/androidx/releases/compose-foundation)

---

## 🔄 Ciclo de vida y navegación

* **Lifecycle Runtime KTX**  
  `androidx.lifecycle:lifecycle-runtime-ktx` — **v2.9.4**  
  👉 Extensiones Kotlin para observar y manejar el ciclo de vida de componentes.  
  🔗 [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)

* **Navigation Compose**  
  `androidx.navigation:navigation-compose` — **v2.9.5**  
  👉 Navegación declarativa entre pantallas en Compose.  
  🔗 [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

---

## 🛠️ Inyección de dependencias

* **Hilt Android**  
  `com.google.dagger:hilt-android` — **v2.57.2**  
  👉 Framework oficial de inyección de dependencias para Android.  
  🔗 [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

* **Hilt Compiler**  
  `com.google.dagger:hilt-compiler` — **v2.57.2**  
  👉 Genera el código necesario para la inyección automática.  
  🔗 [Dagger Hilt](https://dagger.dev/hilt/)

* **Hilt Navigation Compose**  
  `androidx.hilt:hilt-lifecycle-viewmodel-compose` — **v1.3.0**  
  👉 Permite usar `hiltViewModel()` directamente en composables.  
  🔗 [Hilt Navigation Compose](https://developer.android.com/jetpack/androidx/releases/hilt)

---

## 💾 Persistencia de datos

* **DataStore Preferences**  
  `androidx.datastore:datastore-preferences` — **v1.1.7**  
  👉 Reemplazo moderno y seguro de SharedPreferences.  
  🔗 [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

* **Room**
  * `androidx.room:room-runtime`
  * `androidx.room:room-compiler`
  * `androidx.room:room-ktx`
  * `androidx.room:room-paging`  
    👉 Librerías de persistencia de datos basadas en SQLite.  
    🔗 [Room](https://developer.android.com/jetpack/androidx/releases/room)

* **Paging 3**
  * `androidx.paging:paging-runtime-ktx`
  * `androidx.paging:paging-compose`  
    👉 Manejo eficiente de listas paginadas en bases de datos o red.  
    🔗 [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

* **Gson**  
  `com.google.code.gson:gson` — **v2.13.2**  
  👉 Librería para serialización y deserialización de JSON.  
  🔗 [Gson GitHub](https://github.com/google/gson)

* **Apache PDFBox (Android)**  
  `com.tom-roush:pdfbox-android` — **v2.0.27.0**  
  👉 Permite generar y manipular documentos PDF desde Android.  
  🔗 [PDFBox Android](https://github.com/TomRoush/PdfBox-Android)

---

## 📋 Utilidades

* **AndroidX Core KTX**  
  `androidx.core:core-ktx` — **v1.17.0**  
  👉 Extensiones Kotlin para la API base de Android.  
  🔗 [Core KTX](https://developer.android.com/jetpack/androidx/releases/core)

* **Timber**  
  `com.jakewharton.timber:timber` — **v5.0.1**  
  👉 Logging avanzado y limpio para desarrollo en Android.  
  🔗 [Timber GitHub](https://github.com/JakeWharton/timber)

* **Kotlin Serialization**
  * `org.jetbrains.kotlinx:kotlinx-serialization-core`
  * `org.jetbrains.kotlinx:kotlinx-serialization-json`  
    — **v1.9.0**  
    👉 Serialización moderna y multiplataforma para Kotlin.  
    🔗 [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)

---

## 🧪 Testing

* **JUnit 4**  
  `junit:junit` — **v4.13.2**  
  👉 Framework clásico de pruebas unitarias.  
  🔗 [JUnit 4](https://junit.org/junit4/)

* **JUnit 5 (Jupiter)**  
  `org.junit.jupiter:junit-jupiter` — **v6.0.0**  
  👉 Nueva generación de JUnit con características extendidas.  
  🔗 [JUnit 5](https://junit.org/junit5/)

* **AndroidX JUnit**
  * `androidx.test.ext:junit` — **v1.3.0**
  * `androidx.test.ext:junit-ktx` — **v1.3.0**  
    👉 Extensiones de JUnit para entornos Android.  
    🔗 [AndroidX Test](https://developer.android.com/jetpack/androidx/releases/test)

* **Espresso Core**  
  `androidx.test.espresso:espresso-core` — **v3.7.0**  
  👉 Framework para pruebas de UI automatizadas.  
  🔗 [Espresso](https://developer.android.com/training/testing/espresso)

* **Mockito**
  * `org.mockito:mockito-core` — **v5.20.0**
  * `org.mockito.kotlin:mockito-kotlin` — **v6.1.0**  
    👉 Creación de objetos simulados (mocks) para pruebas unitarias.  
    🔗 [Mockito](https://site.mockito.org)

---

✅ **Última actualización:** *19 de Octubre 2025*  
📄 *Archivo sincronizado con versiones del catálogo Gradle (libs.versions.toml)*