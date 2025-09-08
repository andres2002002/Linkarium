# 📐 Arquitectura del Proyecto

## ⚙️ Estructura del Proyecto

```plaintext
app/
└── src/
    └── main/
        ├── java/com/tuapp/enlaces/
        │    ├── core/              # Utilidades, constantes, extensiones
        │    ├── data/              # Capa de datos
        │    │    ├── local/        # Persistencia local (Room, DAOs, entidades)
        │    │    │    ├── model/   # Modelos de datos (Link, Tag, Folder)
        │    │    │    ├── room/    # Base de datos Room
        │    │    │    │    ├── dao/      # Data Access Objects
        │    │    │    │    └── entity/   # Entidades de Room
        │    │    │    └── usecase/ # Casos de uso con Room
        │    │    ├── remote/       # API / servicios remotos (futuro sync)
        │    │    └── repository/   # Implementaciones de repositorios
        │    ├── domain/            # Lógica de negocio
        │    │    ├── model/        # Modelos de dominio (Link, Tag, Folder)
        │    │    └── usecase/      # Casos de uso (ej. GuardarEnlace, ObtenerFavoritos)
        │    ├── ui/                # Interfaz con Jetpack Compose
        │    │    ├── components/   # Composables reutilizables (botones, cards, inputs)
        │    │    ├── screens/      # Pantallas principales
        │    │    │    ├── home/    # Lista de enlaces
        │    │    │    ├── details/ # Detalle de un enlace
        │    │    │    ├── add/     # Agregar nuevo enlace
        │    │    │    └── settings/# Configuración
        │    │    ├── navigation/   # NavHost y rutas
        │    │    ├── utils/        # Utilidades de UI (DateFormatter, UiText)
        │    │    └── theme/        # Colores, tipografía, shapes
        │    └── di/                # Inyección de dependencias (Hilt/Koin)
        └── res/                    # Recursos XML mínimos (íconos, strings)
```

### Notas rápidas:

* **core/**: evita duplicar helpers o constantes. Ejemplo: `DateFormatter`, `UiText`, `Result<T>`.
* **data/**: encapsula todas las fuentes de datos (Room, repositorios, SharedPreferences, APIs).
* **domain/**: define modelos y casos de uso, desacoplados de UI.
* **ui/**: organiza Compose en `screens` (pantallas completas) y `components` (widgets).
* **navigation/**: centraliza rutas y `NavHost`.
* **di/**: módulos de Hilt/Koin.
* **theme/**: tipografía, colores y formas según Compose.

---

## ⚙️ Primeros Pasos

1. **Configurar Hilt**

    * Añadir dependencias de Hilt (`compiler`, `android`, `navigation`).
    * Agregar `ksp` (ver `Dependencies.md`).
    * Crear clase `ApplicationApp`:

      ```kotlin
      @HiltAndroidApp
      class LinkariumApp : Application()
      ```
    * Anotar `MainActivity`:

      ```kotlin
      @AndroidEntryPoint
      class MainActivity : ComponentActivity()
      ```
    * Registrar en `AndroidManifest.xml`:

      ```xml
      <application
          android:name=".LinkariumApp"
          ... />
      ```

2. **Configurar Timber**

    * Inicializar en `LinkariumApp`:

      ```kotlin
      override fun onCreate() {
          super.onCreate()
          if (BuildConfig.DEBUG) {
              Timber.plant(DebugTree())
              Timber.i("onCreate: Timber inicializado")
          } else {
              Timber.plant(CrashReportingTree())
              Timber.i("onCreate: Timber en modo producción")
          }
      }
 
      private class CrashReportingTree : Timber.Tree() {
          override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
              // Integrar con Crashlytics u otro servicio
          }
      }
      ```

3. **Configurar Room**

    * Crear modelos en `/domain/model` (interfaces).
    * Crear entidades en `/data/local/room/entity` con `@Entity`, `@PrimaryKey`, `@ColumnInfo`.
    * Definir `DatabaseContract` en `/data/local/room` (constantes de tablas).
    * Crear base de datos `RoomDatabase` + `TypeConverters` (usar Gson).
    * Implementar DAOs en `/data/local/room/dao`.
    * Implementar data sources en `/data/local/datasource` que traduzcan entidades ↔ modelos de dominio.
    * Implementar repositorios en `/data/repository` para manejo de datos y cache.
    * Registrar en `/di` módulos de Room y repositorios.

   Notas:
      -Se añadio la fun update() para sustituir copy() en los modelos por no poder ser usado en interfaces.
---

## ⚙️ Preparar Navegación y Scaffold

* Centralizar rutas en `/ui/navigation`.
* Definir un `NavHost` único.
* Integrar Scaffold responsivo para adaptarse a distintos tamaños de pantalla.

## ⚙️ Preparar ShowGardenScreen:
Esta pantalla muestra los enlaces (seed) en las collecciones (garden) a forma de lista y los garden en un tabrowScrollable.

* Separamos el código en `ContentScreen` (parte visual) y `ShowGardenScreen` (acceso al viewmodel y obtiene los datos).
* Implementamos el viewmodel en `ShowGardenViewModel` que accede a `LinkGardenRepository`.