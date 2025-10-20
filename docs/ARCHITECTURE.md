## 📚 Índice
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Primeros Pasos](#-primeros-pasos)
- [Navegación y Scaffold](#-preparar-navegación-y-scaffold)
- [Detalles por paquete](#-detalles-de-las-estructuras-del-proyecto)

# 📐 Arquitectura del Proyecto

## 🧩️ Estructura del Proyecto

```plaintext
app/
└── src/
    └── main/
        ├── java/com/tuapp/enlaces/
        │    ├── core/              # Utilidades, constantes, extensiones
        │    ├── data/              # Capa de datos
        │    │    ├── local/        # Persistencia local (Room, DAOs, entidades)
        │    │    │    ├── datasource/ # Data sources Accesos a Daos y validaciones menores
        │    │    │    ├── room/    # Base de datos Room
        │    │    │    │    ├── dao/      # Data Access Objects
        │    │    │    │    ├── entity/   # Entidades de Room
        │    │    │    │    └── migrations/   # Migraciones de Room
        │    │    │    └── usecase/ # Casos de uso con Room
        │    │    ├── remote/       # API / servicios remotos (futuro sync)
        │    │    └── repository/   # Implementaciones de repositorios
        │    ├── domain/            # Lógica de negocio
        │    │    ├── model/        # Modelos de dominio (Link, Tag, Folder)
        │    │    └── usecase/      # Casos de uso como implementaciones de modelos
        │    ├── ui/                # Interfaz con Jetpack Compose
        │    │    ├── components/   # Composables reutilizables (botones, cards, inputs)
        │    │    ├── scaffold/     # Componentes del Scaffold (ScaffoldConfig,, ScaffoldContent, etc.)
        │    │    ├── screens/      # Pantallas principales
        │    │    │    ├── gardenManager/ # Añadir/Editar un Garden
        │    │    │    ├── gardensScreen/ # Lista de Gardens
        │    │    │    ├── plantSeed/     # Agregar nuevo enlace
        │    │    │    ├── showGarden/    # Pantalla con seeds por garden
        │    │    │    └── settings/      # Configuración
        │    │    ├── navigation/   # NavHost y rutas
        │    │    ├── utils/        # Utilidades de UI (DateFormatter, UiText)
        │    │    └── theme/        # Colores, tipografía, shapes
        │    └── di/                # Inyección de dependencias (Hilt/Koin)
        └── res/                    # Recursos XML mínimos (íconos, strings)
```

### Notas rápidas:

* **core/**: evita duplicar helpers o constantes. Ejemplo: `DateFormatter`, `UiText`, `Result<T>`.
* **data/**: encapsula todas las fuentes de datos (Room, repositorios, APIs).
* **domain/**: define modelos y casos de uso, desacoplados de UI.
* **ui/**: organiza Compose en `screens` (pantallas completas) y `components` (widgets).
* **navigation/**: centraliza rutas y `NavHost`.
* **di/**: módulos de Hilt.
* **theme/**: tipografía, colores y formas según Compose.

---

## 🧱 Primeros Pasos

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

    -Se añadió la función `update()` para reemplazar `copy()` en interfaces, ya que `copy()` no puede declararse directamente en ellas. En caso necesario, se implementa una clase Impl para conservar ese comportamiento o mapear datos compuestos.
   ⚠️ Importante: Ningún `Dao` debe realizar transacciones directamente; estas se manejan desde los repositorios.
---

## ⚙️ Preparar Navegación y Scaffold

* Centralizar rutas en `/ui/navigation` en una sealed class con los parametros escenciales (route, title, etc.).
* Se crea un Navhost de navegacion principal con textos temporales para integrar las pantallas mas adelante

# Detalles de las estructuras del proyecto

## 🧭️ ui/navigation

* Estructura de Screens:
    ```kotlin
      sealed class Screens(
      val baseRoute: String,
      @StringRes val normalTitle: Int,
      @StringRes val creativeTitle: Int = normalTitle,
      @DrawableRes val iconSelect: Int? = null,
      @DrawableRes val iconUnselect: Int? = iconSelect,
      val typeScreen: TypeScreen = TypeScreen.Primary
      ) {
      /** Ruta completa (con placeholders si aplica) */
      open val route: String get() = baseRoute
    
      /** Permite detectar rutas dinámicas (por ejemplo "show_seeds/3") */
      open fun matches(route: String?): Boolean =
      route?.substringBefore("?")?.startsWith(baseRoute) == true
      /**
        * Permite crear rutas dinámicas (por ejemplo "show_seeds/3").
        * Tiene valor por defecto -1 que se considera null
        * */
          open fun createRoute(id: Long = -1): String = baseRoute
    
      companion object{
      /** Registro automático de pantallas */
      val allScreens: List<Screens> = listOf(/*screens*/)
            fun fromRoute(route: String?): Screens? {
                return allScreens.firstOrNull { it.matches(route) }
            }
      }
  /*Screens de la aplicacion como dataobject o data class segun sea necesario*/
    }
  ```
  Se usa de esta manera con params dentro de NavHost para pasar index de entidades para editar o mostrar detalles.

  * Estructura de NavHost :
  ```kotlin
      @Composable
    fun NavigationHost(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
    ) {
  //el navController se pasa al provedor local para tenerlo disponibble desde cualquier parte de la app
        CompositionLocalProvider(
        LocalNavigator provides navController,
        LocalWindowSizeClass provides windowSizeClass
        ) {
            NavHost(
            navController = navController,
            startDestination = Screens.ShowGarden.route
            ) {
                /*los composables de la app*/
            }
        }
    }
  ```
* Estructura de TypeScreen:
```kotlin
sealed class TypeScreen(
    open val showTopBar: Boolean,
    open val showBottomBar: Boolean,
    open val showFloatingActionButton: Boolean
) {
    /*tipos de pantalla, ej. primario, secundario, etc*/
}
```
Estas clases son para que las maneje el Scaffold y organizar mejor que se ve en cada pantalla.

un ejemplo de como se vinculan estas estructuras es la siguiente:
    Tomemos la screen `ShowGarden` desde ella se debe poder navegar a `Settings` y `Gardens` ademas de agregar un nuevo enlace.
    por tanto decimos que `ShowGarden` es de tipo primario y deben mostrarse todos los menus, ahora para navegar a añadir un enlace
    debe pasar el parametro de la id para que luego el `ViewModel` de `PlantNewSeed` pueda obtener la entidad correspondiente.
    por tanto debemos pasarlo en la route con `createRoute(id: Long)` y para saber a que pantalla navegar usamos `match` en vez de
    una igualdad de strings asi el scaffold sabe en que pantalla esta, solo teniendo la ruta y sabe que menus mostrar.

## 🎨️ ui/scaffold

* **LinkariumScaffold** 
    Es el componente que organiza todas la partes del scaffol que provee el ScaffoldConfig.
* **ScaffoldConfig**
    Es la configuracion y datos que se muestran segun el `WindowSizeClass` de la pantalla, utiliza un buider para manejo mas comodo.
* **ScaffoldApp**
    Es el componente que se va a mostrar, se contruye un config, se obtiene el viewmodel para comunicacion entre el scaffold
    y las pantallas y se pasa al `LinkariumScaffold`.
* **ScaffoldViewModel**
    Es el viewmodel que contiene los datos reactivos del scaffold como titulos, eventos, etc. pueden pasarse buses para
    comunicacion entre el scaffold y las pantallas de la app.
* **Dialogs**
    es el sistema de dialofos de la app, los mensages estan dados por `MessageValues` que contiene detalles y datos para dar un
    dialogo personalizado, hay configuaraciones personalizadas en `DialogType` para casos de error, confirmacion, info y advertencias.
    los mensajes se transmiten con `MessageBus`

##  💾️ Helpers

* **ClipboardHelper**
  - Helper para copiar y obtener datos del clipboard
  - Usa funciones suspendidas para que se ejecute en un hilo separado
  - `rememberClipboarHelper` es una función de Compose que recuerda el estado del helper

* **UriHelper**
   - Helper para manejar acciones de uri (abrir navegador, compartir, etc)
   - `rememberUriHelper` es una función de Compose que recuerda el estado del helper

## 💾 /data/room/

Todas las Constantes de Room estan en `/data/local/room/DatabaseContract`
para centralizar las tablas y nombres de campos y evitar errores de tipeo y confusiones.

* **entity**
    - `LinkGardenEntity`: Contiene campos de name y dascription para una coleccion de `LinkSeedEntity`.
    - `LinkSeedEntity`: Contiene datos sobre el/los enlaces vinculados, esta vinculada a una `LinkGardenEntity`.
    - `LinkEntryEntity`: Contiene campos para un label, notes y una uri, esta vinculada a una `LinkSeedEntity`.
    - `LinkTagEntity`: Contiene campos de name para el tag y esta vinculada a una `LinkSeedEntity`.
    `LinkEntryEntity` y `LinkTagEntity` son entidades para una `LinkSeed`del modelo, pero se separan en room para mejorar
    el rendimiento de busquedas y evitar convertir datos en la databse.
* **dao**
  > Los dao no manejan directamente transactions, eso lo relegan a los repositorios.
    - `LinkGardenDao`: Dao para `LinkGardenEntity`.
    - `LinkSeedDao`: Dao para `LinkSeedEntity`, tiene un metodo para obtener los `LinkSeedEntity` de una `LinkGardenEntity` paginados y un metodo suspendido para exportaciones sin paginar.
    - `LinkEntryDao`: Dao para `LinkEntryEntity`.
    - `LinkTagDao`: Dao para `LinkTagEntity`.
* **datasource**
  > Los DataSource hacen de interediario entre los dao y los repositorios, hacen pequeñas transformaciones simples entre dominio y entidad.
    - `LinkGardenDataSource`: Data source para `LinkGardenEntity`.
    - `LinkSeedDataSource`: Data source para `LinkSeedEntity`.
    - `LinkEntryDataSource`: Data source para `LinkEntryEntity`.
    - `LinkTagDataSource`: Data source para `LinkTagEntity`.
* **repository**
  > Los repositorios manejan transformaciones y mapeos mas complejos entre entidades y modelos de dominio, incluyendo los transactions.
    - `LinkGardenRepository`: Repositorio para `LinkGardenEntity`.
    - `LinkSeedRepository`: Repositorio para `LinkSeedEntity`.
    - `LinkEntryRepository`: Repositorio para `LinkEntryEntity`.
    - `LinkTagRepository`: Repositorio para `LinkTagEntity`.

## 📏 Convenciones de código

1. **Room**
* Las entidades de room deben terminar en Entity.
* Los Dao deben ser interfaces y terminar en Dao.
* Los data sources deben ser interfaces y terminar en DataSource.
* Los repositorios deben ser interfaces y terminar en Repository.
* Las implementaciones de interfaz son de la forma [interface_name]Impl.
* No deben tratarse `@Transaction` en los dao´s se maneja en los repositorios.

2. **Model**
* Los modelos base deben ser interfaces.
* Si es necesario instanciar un modelo (por ejemplo `LinkSeed` que se compone de tres entities) se hace una data class [model]Impl en /usecase/.

3. **Others**
    * Las pantallas se guardan con nombre de la forma [screen_name]Screen.
    * El `ViewModel` de una Pantalla se guarda en el mismo paquete que la pantalla y se guardan con nombres de la forma [screen_name]ViewModel.
    * `Flow` se utiliza para exponer estados reactivos y consistentes con el ciclo de vida de los composables.
    * Se usa `suspend fun` en caso de procesos secundarios, como guardados en segundo plano o exportaciones e importaciones de datos.