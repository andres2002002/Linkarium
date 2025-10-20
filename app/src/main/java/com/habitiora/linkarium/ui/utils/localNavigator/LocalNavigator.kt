package com.habitiora.linkarium.ui.utils.localNavigator

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.navigation.TypeScreen

val LocalNavigator = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

/**
 * Navegación centralizada que decide el comportamiento según el tipo de la pantalla.
 *
 * Comportamiento general:
 *  - Evita navegaciones redundantes cuando ya estamos en la misma ruta (salvo para Tertiary).
 *  - Aplica distintas configuraciones de `NavOptions` según [TypeScreen].
 *
 * Notas sobre flags usados:
 *  - popUpTo(...){ saveState = true }: retrocede hasta la start destination
 *    y guarda el estado de las entradas poppeadas (útil para tabs/PRIMARY).
 *  - launchSingleTop: evita crear múltiples instancias del mismo destino en la cima.
 *  - restoreState = true: intenta restaurar el estado previamente salvado para la ruta.
 *
 * @receiver NavHostController Controlador de navegación (compose / navigation component).
 * @param screen Pantalla destino que contiene su ruta y su tipo.
 *
 * EJEMPLOS:
 * ```
 * // Navegar a la pantalla principal "home"
 * navController.navigateTo(Screens.ShowGarden)
 * ```
 *
 * COMPORTAMIENTOS ESPECIALES:
 *  - Si ya estamos en la misma ruta, la función retorna sin navegar para PRIMARY/SECONDARY/RESET.
 *    Para Tertiary se permite navegar aunque la ruta coincida (útil para abrir múltiples
 *    instancias de un detalle, por ejemplo).
 *
 * PRECAUCIONES:
 *  - `saveState` / `restoreState` funcionan entre destinos que comparten la misma
 *    route y que fueron creados/restaurados según el Navigation component. Si tienes
 *    lógica compleja de estado (ViewModel con scope distinto), asegúrate de que
 *    la restauración sea coherente con tu gestión de ViewModels.
 *
 *  - Si usas argumentos en la ruta (p. ej. "profile/{id}"), asegúrate de pasar la ruta
 *    completa con los argumentos resueltos ("profile/123") o adaptar la lógica para
 *    usar Destinations con navArgs.
 */
fun NavHostController.navigateToScreen(screen: Screens) = navigateToInternal(screen)

fun NavHostController.navigateToRoute(route: String) {
    val screen = Screens.fromRoute(route)
    screen?.let { navigateToInternal(it, route) }
}

private fun NavHostController.navigateToInternal(
    screen: Screens,
    route: String? = null
){
    // Ruta actual para comparar y evitar duplicados
    val currentRoute = currentBackStackEntry?.destination?.route

    // Evita navegar si ya estamos en la misma ruta (salvo para TERTIARY, que permite duplicados)
    if (currentRoute == screen.route && screen.typeScreen != TypeScreen.Tertiary) return

    val navRoute = route ?: screen.route

    when (screen.typeScreen) {
        TypeScreen.Primary -> {
            // Ideal para tabs / pantallas raíz:
            // - popUpTo start destination para mantener una sola rama principal
            // - saveState para poder restaurar el estado de una tab cuando volvamos a ella
            // - launchSingleTop para evitar duplicados
            // - restoreState para recuperar estado si existía
            navigate(navRoute) {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        TypeScreen.Secondary -> {
            // Mantén el back stack (permite volver atrás).
            // Usamos singleTop para evitar duplicarlos si se pulsa repetidamente.
            // Permitimos restoreState por si queremos recuperar UI previa.
            navigate(navRoute) {
                launchSingleTop = true
                restoreState = true
            }
        }

        TypeScreen.Tertiary -> {
            // Flujo efímero / detalle:
            // - No usamos launchSingleTop: permitimos instancias repetidas
            // - No intentamos restoreState automáticamente
            navigate(navRoute) {
                launchSingleTop = false
                restoreState = false
            }
        }

        else -> {
            // Reinicia el flujo de navegación: elimina todo el back stack
            // y deja la nueva pantalla como única entrada.
            navigate(navRoute) {
                popUpTo(graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}