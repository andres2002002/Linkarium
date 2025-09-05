

el proyecto esta estructurado:
app/
└── src/
└── main/
├── java/com/tuapp/enlaces/
│    ├── core/              # Cosas comunes (utilidades, constantes, extensiones)
│    ├── data/              # Capa de datos
│    │    ├── local/        # Room DB, DAOs, entidades
│    │    ├── remote/       # API/servicios (si algún día agregas sync)
│    │    └── repository/   # Implementaciones de repositorios
│    ├── domain/            # Lógica de negocio
│    │    ├── model/        # Modelos de dominio (Link, Tag, Folder)
│    │    └── usecase/      # Casos de uso (ej. GuardarEnlace, ObtenerFavoritos)
│    ├── ui/                # Todo lo de Compose
│    │    ├── components/   # Composables reutilizables (botones, cards, inputs)
│    │    ├── screens/      # Pantallas principales
│    │    │    ├── home/    # Lista de enlaces
│    │    │    ├── details/ # Detalle de un enlace
│    │    │    ├── add/     # Agregar nuevo enlace
│    │    │    └── settings/# Configuración
│    │    ├── navigation/   # NavHost, rutas
│    │    └── theme/        # Colores, tipografía, shapes
│    └── di/                # Inyección de dependencias (Hilt/Koin)
└── res/                    # Recursos XML (solo lo mínimo, como íconos, strings)

core/: evita duplicar helpers o constantes. Ej. DateFormatter, UiText, Result<T>.

data/: encapsula todo lo que toca datos (Room, SharedPreferences, repositorio).

domain/: independencia de la UI. Define qué es un "Link" y qué operaciones soporta.

ui/: aquí vive Compose. Dividido en screens (pantallas completas) y components (widgets reutilizables).

navigation/: centraliza rutas y el NavHost.

di/: mantiene orden en Hilt o Koin para inyección.

theme/: Compose recomienda tener tipografía, colores y formas separados.