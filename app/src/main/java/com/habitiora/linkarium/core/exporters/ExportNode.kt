package com.habitiora.linkarium.core.exporters

import kotlin.reflect.KProperty1

sealed interface ExportNode<T>{
    val header: String

    /**
     * Representa un nodo en la jerarquía de exportación, asignando una propiedad específica de tipo [T] a un único valor de salida.
     *
     * Ejemplo
     * ```
     * val schema = ExportSchema(
     *     listOf(
     *         ExportField("id", LinkSeedEntity::id),
     *         ExportField("name", LinkSeedEntity::name),
     *         ExportField("order", LinkSeedEntity::order),
     *         ExportField("isFavorite", LinkSeedEntity::isFavorite)
     *         )
     * ```
     *
     * Luego en un String que contenga "... @id ..." se puede intercambiar por el valor que tenga una exidad.
     *
     * @param T El tipo de objeto que se está exportando.
     * @property header El nombre de la columna o etiqueta para este campo en el formato de exportación.
     * @property property La referencia de propiedad de reflexión utilizada para extraer el valor de una instancia de [T].
     * @property formatter Una función de transformación que convierte el valor de propiedad en una representación de cadena.
     * Por defecto, se usa [toString] o una cadena vacía si el valor es nulo.
     *
     * @see ExportSchema
     */
    data class ExportField<T>(
        override val header: String,
        val property: KProperty1<T, *>,
        val formatter: (Any?) -> String = { it?.toString() ?: "" }
    ) : ExportNode<T>{
        fun getValue(entity: T): String = formatter(property.get(entity))
    }

    data class ExportNestedList<T, C>(
        override val header: String,
        val property: KProperty1<T, List<C>>,
        val schema: ExportSchema<C>
    ) : ExportNode<T>

    data class ExportNestedEntity<T, C>(
        override val header: String,
        val property: KProperty1<T, C>,
        val schema: ExportSchema<C>
    ) : ExportNode<T>
}
/*
    (
    val header: String,
    val property: KProperty1<T, *>,
    val formatter: (Any?) -> String = { it?.toString() ?: "" }
)
*/
