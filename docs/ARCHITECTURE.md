## 📚 Índice
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Primeros Pasos](#-primeros-pasos)
- [Navegación y Scaffold](#-preparar-navegación-y-scaffold)
- [Detalles por paquete](#-detalles-de-las-estructuras-del-proyecto)
- [Convenciones de código](#-convenciones-de-código)

# 📐 Arquitectura del Proyecto

Este proyecto sigue una arquitectura limpia (Clean Architecture) adaptada para Android, utilizando Jetpack Compose para la UI. El objetivo es separar las responsabilidades, facilitar el mantenimiento y mejorar la escalabilidad.

La arquitectura se divide en tres capas principales:

1.  **Capa de UI (Interfaz de Usuario)**: Compuesta por `Activity`, `Composables` (pantallas y componentes) y `ViewModels`. Es responsable de mostrar los datos y de capturar las interacciones del usuario.
2.  **Capa de Dominio (Domain)**: Contiene la lógica de negocio pura. Define los modelos de datos (`LinkGarden`, `LinkSeed`) y los casos de uso. Esta capa es independiente de Android y de cualquier framework de UI o base de datos.
3.  **Capa de Datos (Data)**: Gestiona todas las fuentes de datos. Implementa los repositorios definidos en la capa de dominio y se encarga de obtener datos de Room, DataStore o futuras APIs remotas.

## 🧩️ Estructura del Proyecto

```plaintext
app/
└── src/
    └── main/
        ├── java/com/habitiora/linkarium/
        │    ├── core/              # Utilidades, constantes, extensiones, helpers
        │    ├── data/              # Capa de datos
        │    │    ├── local/        # Persistencia local (Room, DAOs, DataStore)
        │    │    │    ├── datasource/ # Fuentes de datos que acceden a los DAOs
        │    │    │    └── room/       # Configuración de Room (DB, DAOs, Entidades)
        │    │    ├── exporters/      # Lógica para exportar datos (PDF, JSON)
        │    │    └── repository/   # Implementaciones de repositorios
        │    ├── domain/            # Lógica de negocio
        │    │    ├── model/        # Modelos de dominio (LinkGarden, LinkSeed)
        │    │    └── usecase/      # Casos de uso (lógica compleja)
        │    ├── ui/                # Interfaz con Jetpack Compose
        │    │    ├── components/   # Composables reutilizables
        │    │    ├── navigation/   # NavHost y rutas de navegación
        │    │    ├── scaffold/     # Componentes del Scaffold principal
        │    │    ├── screens/      # Pantallas de la aplicación
        │    │    └── theme/        # Tema de la aplicación (colores, tipografía)
        │    └── di/                # Inyección de dependencias con Hilt
        └── res/                    # Recursos XML (íconos, strings, etc.)
```

### Notas rápidas:

*   **`core/`**: Evita duplicar helpers o constantes. Ideal para extensiones, `UiText`, `Result<T>`.
*   **`data/`**: Encapsula todas las fuentes de datos (Room, repositorios, APIs).
*   **`domain/`**: Define modelos y casos de uso, desacoplado de la UI y los datos.
*   **`ui/`**: Organiza el código de Jetpack Compose en `screens` y `components` reutilizables.
*   **`navigation/`**: Centraliza la lógica de navegación, rutas y el `NavHost`.
*   **`di/`**: Contiene los módulos de Hilt para la inyección de dependencias.

---

## 🧱 Primeros Pasos

1.  **Configurar Hilt**

    *   Añadir dependencias de Hilt y KSP.
    *   Crear una clase `Application` anotada con `@HiltAndroidApp`.
    *   Anotar `MainActivity` con `@AndroidEntryPoint`.
    *   Registrar la clase `Application` en el `AndroidManifest.xml`.

2.  **Configurar Timber**

    *   Inicializar en la clase `Application` para diferenciar entre builds de `DEBUG` y `release`.

3.  **Configurar Room**

    *   Definir modelos en `/domain/model` como interfaces.
    *   Crear entidades en `/data/local/room/entity` que representen las tablas de la base de datos.
    *   Definir un `DatabaseContract` con constantes para nombres de tablas y columnas.
    *   Implementar los `DAO` (Data Access Objects) para el acceso a la base de datos.
    *   Implementar `DataSource` que actúan como intermediarios con los DAOs.
    *   Implementar `Repository` que orquestan las fuentes de datos y manejan las transacciones.
    *   Registrar todo en módulos de Hilt en la carpeta `/di`.

> ⚠️ **Importante**: Ningún `Dao` debe realizar transacciones (`@Transaction`) directamente. Esta lógica se delega a los repositorios para mantener la cohesión y la claridad.

---

## ⚙️ Preparar Navegación y Scaffold

*   **Navegación**: Se centralizan las rutas en una `sealed class` dentro de `/ui/navigation`. Esto permite definir rutas base, argumentos y títulos de pantalla en un solo lugar.
*   **Scaffold**: Se utiliza un `ScaffoldViewModel` para comunicar eventos y cambios de estado (como el título de la pantalla o la aparición de diálogos) entre las pantallas y el `LinkariumScaffold` principal.

# Detalles de las estructuras del proyecto

## 🧭️ ui/navigation

*   **`Screens`**: `sealed class` que define cada pantalla con su ruta, título y tipo. Incluye funciones para construir rutas dinámicas (ej. `show_seeds/3`) y para identificar la pantalla actual a partir de una ruta.
*   **`NavigationHost`**: `Composable` que contiene el `NavHost` y provee el `NavController` a través de un `CompositionLocal` para que esté disponible en cualquier parte de la UI.
*   **`TypeScreen`**: `sealed class` que define las características del Scaffold para cada tipo de pantalla (ej. si muestra TopBar, BottomBar, etc.).

## 🎨️ ui/scaffold

*   **`LinkariumScaffold`**: El `Composable` que organiza la estructura visual principal (TopAppBar, BottomNavigation, FAB) basándose en la configuración recibida.
*   **`ScaffoldConfig`**: Clase que define la configuración del Scaffold (títulos, acciones, menús) adaptándose al tamaño de la pantalla (`WindowSizeClass`).
*   **`ScaffoldViewModel`**: `ViewModel` que gestiona el estado reactivo del Scaffold, permitiendo a las pantallas modificar el título o solicitar la muestra de diálogos de forma desacoplada.
*   **`Dialogs`**: Sistema centralizado para mostrar diálogos. Se basa en un `MessageBus` (un `SharedFlow`) para emitir solicitudes de diálogo desde cualquier ViewModel.

## 🛠️ core/ (Helpers)

*   **`ClipboardHelper`**: Utilidad para interactuar con el portapapeles de forma asíncrona.
*   **`UriHelper`**: Facilita la interacción con URIs, como abrir enlaces en el navegador o compartir contenido.
*   **`ExportFormatters`**: Contiene la lógica para dar formato a los datos antes de ser exportados, como convertir una lista de `LinkSeed` a un string HTML.

## 💾 /data/

### `data/local/room`

*   **`DatabaseContract`**: Centraliza los nombres de tablas y columnas para evitar errores de tipeo.
*   **Entidades (`entity/`)**: Representan las tablas de la base de datos.
    *   `LinkGardenEntity`: Una colección de enlaces.
    *   `LinkSeedEntity`: La unidad principal, que agrupa la información de un enlace.
    *   `LinkEntryEntity`: Un enlace individual (URI, label, notas) dentro de un `LinkSeed`.
    *   `LinkTagEntity`: Una etiqueta asociada a un `LinkSeed`.
*   **DAOs (`dao/`)**: Interfaces con métodos para acceder a la base de datos (`@Query`, `@Insert`, etc.). No contienen lógica de transacción.

### `data/local/datasource`

*   Intermediarios entre los DAOs y los repositorios. Realizan transformaciones simples entre los modelos de dominio y las entidades de Room.

### `data/repository`

*   Implementaciones de las interfaces del dominio. Orquestan las fuentes de datos (`DataSource`), manejan la lógica de transacciones (`@Transaction`) y transforman los datos de las entidades a modelos de dominio complejos.

### `data/exporters`

*   **`TemplateEngine`**: Motor de plantillas simple para reemplazar placeholders en archivos de texto (ej. plantillas HTML para exportaciones).
*   **Schemas**: Definen la estructura de los datos para la importación y exportación (ej. `LinkGardenAggregateSchema` para JSON).

## 🧠 /domain/model

Modelos de negocio puros, definidos como interfaces para maximizar el desacoplamiento.

*   **`LinkGarden`**: Representa una colección o "jardín" de enlaces.
*   **`LinkSeed`**: La unidad principal de un enlace guardado, que puede contener múltiples `LinkEntry` y `LinkTag`.
*   **`LinkEntry`**: Un enlace específico con su URI, título y notas.
*   **`LinkTag`**: Una etiqueta para clasificar enlaces.
*   **`UserPreferences`**: Modela las preferencias del usuario guardadas en DataStore.
*   **`Exporter`**: Define la interfaz para diferentes estrategias de exportación (PDF, JSON).

## 📏 Convenciones de código

1.  **Room**
    *   Entidades: `[Nombre]Entity`
    *   DAOs: `[Nombre]Dao` (interfaz)
    *   DataSource: `[Nombre]DataSource` (interfaz) / `[Nombre]DataSourceImpl` (clase)
    *   Repositorio: `[Nombre]Repository` (interfaz) / `[Nombre]RepositoryImpl` (clase)
    *   Las transacciones (`@Transaction`) se manejan exclusivamente en los `RepositoryImpl`.

2.  **Dominio**
    *   Los modelos de negocio son interfaces para promover el desacoplamiento.
    *   Si se necesita una instancia concreta para lógica de negocio o mapeo, se crea una `data class` en el paquete `/usecase/` que implemente la interfaz del modelo.

3.  **UI**
    *   Pantallas: `[Nombre]Screen.kt`
    *   ViewModels: `[Nombre]ViewModel.kt`, ubicados en el mismo paquete que su pantalla.
    *   Se utiliza `StateFlow` para exponer el estado de la UI desde el ViewModel a los `Composables`.
