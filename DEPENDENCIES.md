# 📦 Dependencias del Proyecto

Este documento describe las dependencias utilizadas en el proyecto Android, su propósito principal y las versiones actualmente configuradas en `libs.versions.toml`.

---

## 🔧 Plugins

- **Android Gradle Plugin**  
  `com.android.application` — v8.13.0
  > Permite compilar y empaquetar aplicaciones Android.

- **Kotlin Android Plugin**  
  `org.jetbrains.kotlin.android` — v2.2.10
  > Añade soporte de Kotlin en proyectos Android.

- **Compose Compiler Plugin**  
  `org.jetbrains.kotlin.plugin.compose` — v2.2.10
  > Soporte del compilador de Kotlin para Jetpack Compose.

- **Hilt Android Plugin**  
  `com.google.dagger.hilt.android` — v2.57.1
  > Integración de Hilt para inyección de dependencias en Android.

---

## 🖥️ AndroidX & Jetpack

- **Core KTX**  
  `androidx.core:core-ktx` — v1.17.0
  > Extensiones de Kotlin para componentes básicos de Android.

- **Lifecycle Runtime KTX**  
  `androidx.lifecycle:lifecycle-runtime-ktx` — v2.9.3
  > Manejo del ciclo de vida con coroutines y LiveData.

- **Activity Compose**  
  `androidx.activity:activity-compose` — v1.10.1
  > Integración de actividades con Jetpack Compose.

- **Navigation Compose**  
  `androidx.navigation:navigation-compose` — v2.9.3
  > Navegación declarativa en Compose.

- **Hilt Navigation Compose**  
  `androidx.hilt:hilt-navigation-compose` — v1.2.0
  > Integración de Hilt con el sistema de navegación de Compose.

- **DataStore Preferences**  
  `androidx.datastore:datastore-preferences` — v1.1.7
  > Reemplazo moderno de SharedPreferences, persistencia de datos clave-valor.

---

## 🎨 Jetpack Compose

- **Compose BOM**  
  `androidx.compose:compose-bom` — v2025.08.01
  > Asegura compatibilidad entre las librerías de Compose.

- **UI**  
  `androidx.compose.ui:ui`
  > Componentes básicos de UI en Compose.

- **Graphics**  
  `androidx.compose.ui:ui-graphics`
  > Utilidades gráficas para Compose.

- **Tooling & Preview**  
  `androidx.compose.ui:ui-tooling`  
  `androidx.compose.ui:ui-tooling-preview`
  > Herramientas para vista previa y depuración.

- **Testing**  
  `androidx.compose.ui:ui-test-junit4`  
  `androidx.compose.ui:ui-test-manifest`
  > Dependencias para pruebas de UI en Compose.

- **Foundation**  
  `androidx.compose.foundation:foundation`
  > Elementos de UI fundamentales (layouts, gestos, scroll, etc.).

- **Material 3**  
  `androidx.compose.material3:material3`
  > Implementación de Material Design 3.

- **Material 3 Window Size Class**  
  `androidx.compose.material3:material3-window-size-class`
  > Utilidades para diseñar interfaces responsivas.

---

## 🛠️ Inyección de dependencias

- **Hilt Android**  
  `com.google.dagger:hilt-android` — v2.57.1
  > Framework de inyección de dependencias en Android.

- **Hilt Compiler (KAPT)**  
  `com.google.dagger:hilt-compiler` — v2.57.1
  > Genera el código necesario para la inyección con Hilt.

---

## 📝 Logging

- **Timber**  
  `com.jakewharton.timber:timber` — v5.0.1
  > Librería ligera para logging estructurado en Android.

---

## 🧪 Testing

- **JUnit 4**  
  `junit:junit` — v4.13.2
  > Framework principal para pruebas unitarias en Java/Kotlin.

- **JUnit 5 (Jupiter)**  
  `org.junit.jupiter:junit-jupiter` — v5.13.4
  > Versión moderna de JUnit con nuevas APIs de pruebas.

- **AndroidX JUnit**  
  `androidx.test.ext:junit` — v1.3.0
  > Extensión de JUnit para pruebas instrumentadas en Android.

- **Espresso Core**  
  `androidx.test.espresso:espresso-core` — v3.7.0
  > Framework para pruebas de UI en Android.

---

## 📊 Diagrama de dependencias (Mermaid)

```mermaid
flowchart TD

  subgraph AndroidX/Jetpack
    core["Core KTX"]
    lifecycle["Lifecycle Runtime KTX"]
    activity["Activity Compose"]
    navigation["Navigation Compose"]
    hiltNav["Hilt Navigation Compose"]
    datastore["DataStore Preferences"]
  end

  subgraph Compose
    bom["Compose BOM"]
    ui["UI"]
    graphics["Graphics"]
    tooling["Tooling / Preview"]
    testing["UI Testing"]
    foundation["Foundation"]
    material3["Material 3"]
    windowSize["Window Size Class"]
  end

  subgraph DI
    hilt["Hilt Android"]
    compiler["Hilt Compiler"]
  end

  subgraph Testing
    junit4["JUnit 4"]
    junit5["JUnit 5 (Jupiter)"]
    extJunit["AndroidX JUnit"]
    espresso["Espresso Core"]
  end

  logging["Timber"]

  %% Relaciones
  activity --> ui
  navigation --> ui
  hiltNav --> hilt
  hiltNav --> navigation
  compiler --> hilt
  datastore --> core
  lifecycle --> core

  ui --> bom
  graphics --> bom
  tooling --> bom
  testing --> bom
  foundation --> bom
  material3 --> bom
  windowSize --> material3
