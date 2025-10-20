# 🌱 Linkarium

**Linkarium** es una aplicación Android desarrollada con **Jetpack Compose** y **Kotlin**, cuyo objetivo es ofrecer un espacio organizado y visualmente atractivo para gestionar colecciones de ideas, enlaces o contenidos relacionados con proyectos personales. Su diseño modular y su arquitectura moderna permiten una experiencia fluida, adaptable y fácilmente escalable.

---

## 🚀 Características principales

* **Gestión del estado con ViewModel y Flow**, garantizando actualizaciones reactivas y eficientes.
* **Interfaz moderna en Jetpack Compose**, con animaciones fluidas y componentes reutilizables.
* **Persistencia local con Room**, respaldada por un sistema de exportación e importación de datos.

---

## 🧩 Arquitectura

El proyecto sigue el enfoque **MVVM (Model-View-ViewModel)**, complementado con principios de Clean Architecture:

```
app/
 ├─ ui/                # Pantallas y componentes Jetpack Compose
 ├─ domain/            # Casos de uso y modelos de negocio
 ├─ data/              # Repositorios, fuentes de datos locales y remotas
 ├─ di/                # Configuración de Hilt (inyección de dependencias)
 ├─ utils/             # Utilidades y extensiones
 └─ core/              # Configuraciones base y lógica compartida
```

---

## 🧠 Tecnologías utilizadas

* **Kotlin** — Lenguaje principal.
* **Jetpack Compose** — UI declarativa.
* **Room** — Base de datos local.
* **Hilt** — Inyección de dependencias.
* **Paging 3** — Manejo eficiente de listas grandes.
* **Gson** — Serialización y deserialización de datos.
* **Coroutines + Flow** — Programación asíncrona y reactiva.

> Consulta las librerias utilizadas en el archivo [DEPENDENCIES](DEPENDENCIES.md) o en [`libs.versions.toml`](libs.versions.toml).

---

## ⚙️ Configuración del proyecto

1. Clona el repositorio:

   ```bash
   git clone https://github.com/andres2002002/Linkarium
   ```
2. Abre el proyecto en **Android Studio (Narwal o superior)**.
3. Sincroniza las dependencias de Gradle.
5. Ejecuta la aplicación en un emulador o dispositivo físico.

---

## 🌤️ Exportación y respaldo

Linkarium permite **exportar tus datos en formato PDF o JSON**.

---

## 🧪 Pruebas y desarrollo

* Las funciones críticas están cubiertas con **tests unitarios y de integración**.
* Se utiliza **MockK** para simular dependencias.
* Las vistas pueden probarse de forma aislada gracias a la arquitectura modular.

---

## 📄 Licencia

Este proyecto está licenciado bajo la **MIT License**. Consulta el archivo [`LICENSE`](LICENSE) para más detalles.

---

## 💬 Contribuciones

¡Las contribuciones son bienvenidas! Si tienes una idea o mejora, abre un *issue* o envía un *pull request*.

---

## ✨ Autor

**Desarrollado por andres2002002**
Proyecto en evolución constante como parte de **Desarrollo Linkarium** 🌿
