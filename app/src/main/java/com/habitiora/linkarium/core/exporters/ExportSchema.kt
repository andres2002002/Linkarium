package com.habitiora.linkarium.core.exporters

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class ExportSchema<T>(
    private val fields: List<ExportNode<T>>
) {
    companion object {
        private val tokenRegex: Regex
            get() = "@([a-zA-Z0-9_]+)".toRegex()
        val listRegex: Regex
            get() = "#([a-zA-Z0-9_]+)".toRegex()
        private val listKeyRegex = "#([a-zA-Z0-9_]+):([a-zA-Z0-9_]+)".toRegex()

        // Helper simple para escapar caracteres especiales en strings JSON
        private fun escapeJson(raw: String): String {
            return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        }
    }

    // Mapa rápido para buscar nodos por su header original
    private val fieldMap: Map<String, ExportNode<T>> = fields.associateBy { it.header }

    // Mapa de los campos simples para reemplazo de tokens (@id, @name)
    private val simpleFields: List<ExportNode.ExportField<T>> = fields.filterIsInstance<ExportNode.ExportField<T>>()

    /**
     * Procesa un template JSON y retorna un nuevo JSON string con los datos inyectados.
     */
    fun generateJsonFromTemplate(entity: T, templateJsonString: String): String {
        val templateElement = Json.parseToJsonElement(templateJsonString)
        val resultElement = processElement(entity, templateElement)
        return Json { prettyPrint = true }.encodeToString(resultElement)
    }

    private fun processElement(entity: T, template: JsonElement): JsonElement {
        return when (template) {
            is JsonObject -> processJsonObject(entity, template)
            is JsonPrimitive -> {
                if (template.isString) {
                    // Si es string, hacemos reemplazo de tokens: "Hola @name"
                    JsonPrimitive(replaceTokens(template.content, entity))
                } else {
                    template // Números o booleanos se quedan igual
                }
            }
            is JsonArray -> template // Arrays directos en el template no suelen tener lógica, se devuelven tal cual
        }
    }

    private fun processJsonObject(entity: T, jsonObject: JsonObject): JsonObject {
        val resultMap = mutableMapOf<String, JsonElement>()

        jsonObject.entries.forEach { (key, element) ->
            // 1. Analizamos la llave: "source:target" o solo "target"
            val parts = key.split(":", limit = 2)
            val sourceHeader = parts.first()
            val targetKey = if (parts.size > 1) parts[1] else sourceHeader

            // 2. Buscamos si existe ese nodo en nuestro Schema
            val node = fieldMap[sourceHeader]

            when (node) {
                // Caso A: Es una Lista Anidada (detectado por el Schema)
                is ExportNode.ExportNestedList<T, *> -> {
                    // Buscamos la definición del schema interno en el template
                    // El usuario usa "$schema" dentro del objeto para definir la estructura de los hijos
                    val innerTemplate = (element as? JsonObject)?.get("\$schema")

                    if (innerTemplate != null) {
                        resultMap[targetKey] = processNestedList(node, entity, innerTemplate)
                    } else {
                        // Si no hay $schema, intentamos serializar por defecto o ignorar
                        resultMap[targetKey] = JsonArray(emptyList())
                    }
                }

                // Caso B: Es una Entidad Anidada
                is ExportNode.ExportNestedEntity<T, *> -> {
                    resultMap[targetKey] = processNestedEntity(node, entity, element)
                }

                // Caso C: Es un campo simple o un campo que no existe en el schema (custom fields)
                // Si el nodo es null (no existe en schema), asumimos que es un campo estático/custom del template
                // y procesamos su valor buscando tokens.
                else -> {
                    // Usamos la key original del template (key completa) si no hubo match con source
                    // Si hubo match con Field, usamos targetKey.
                    val finalKey = if (node != null) targetKey else key
                    resultMap[finalKey] = processElement(entity, element)
                }
            }
        }
        return JsonObject(resultMap)
    }

    // --- Helpers de Recursividad ---

    @Suppress("UNCHECKED_CAST")
    private fun <C> processNestedList(
        node: ExportNode.ExportNestedList<T, C>,
        parentEntity: T,
        template: JsonElement
    ): JsonArray {
        val list = node.property.get(parentEntity)
        val jsonItems = list.map { childItem ->
            // Recursión: El schema del hijo procesa el template del hijo
            // OJO: Aquí 'processElement' debe ser accesible, o llamamos recursivamente a lógica interna
            // Como 'node.schema' es otro ExportSchema, necesitamos exponer un método interno o usar helper.
            // Simplificación: Duplicamos lógica o hacemos público el processElement en la clase.
            // Para mantener encapsulamiento, delegamos al schema hijo:
            node.schema.processInternal(childItem, template)
        }
        return JsonArray(jsonItems)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <C> processNestedEntity(
        node: ExportNode.ExportNestedEntity<T, C>,
        parentEntity: T,
        template: JsonElement
    ): JsonElement {
        val childEntity = node.property.get(parentEntity)
        return if (childEntity == null) {
            JsonNull
        } else {
            node.schema.processInternal(childEntity, template)
        }
    }

    // Método helper para permitir la recursión entre Schemas distintos
    fun processInternal(entity: T, template: JsonElement): JsonElement {
        return processElement(entity, template)
    }

    private fun replaceTokens(templateString: String, entity: T): String {
        var result = templateString
        // Iteramos solo sobre los campos simples definidos en este esquema
        simpleFields.forEach { field ->
            val token = "@${field.header}"
            if (result.contains(token)) {
                result = result.replace(token, field.getValue(entity))
            }
        }
        return result
    }
}