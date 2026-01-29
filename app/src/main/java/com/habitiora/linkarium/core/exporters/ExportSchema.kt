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

    private val jsonConfig = Json {
        prettyPrint = true // False para ahorrar bytes en archivos grandes, o true si es para lectura humana
        encodeDefaults = true
    }


    // Mapa rápido para buscar nodos por su header original
    private val fieldMap: Map<String, ExportNode<T>> = fields.associateBy { it.header }

    // Mapa de los campos simples para reemplazo de tokens (@id, @name)
    private val simpleFields: List<ExportNode.ExportField<T>> = fields.filterIsInstance<ExportNode.ExportField<T>>()

    /**
     * 1. Prepara el template una sola vez.
     * Esto evita parsear el String miles de veces.
     */
    fun prepareTemplate(templateJsonString: String): JsonElement {
        return Json.parseToJsonElement(templateJsonString)
    }

    /**
     * 2. Procesa una entidad usando un template ya preparado.
     * Retorna el String JSON de esa única entidad.
     */
    fun processEntity(entity: T, templateElement: JsonElement): String {
        val resultElement = processElement(entity, templateElement)
        return jsonConfig.encodeToString(resultElement)
    }

    // --- Lógica Interna (La misma de antes, pero adaptada a recursividad eficiente) ---

    private fun processElement(entity: T, template: JsonElement): JsonElement {
        return when (template) {
            is JsonObject -> processJsonObject(entity, template)
            is JsonPrimitive -> {
                if (template.isString) JsonPrimitive(replaceTokens(template.content, entity))
                else template
            }
            is JsonArray -> template // Arrays estáticos se quedan igual
        }
    }

    private fun processJsonObject(entity: T, jsonObject: JsonObject): JsonObject {
        val resultMap = mutableMapOf<String, JsonElement>()

        jsonObject.entries.forEach { (key, element) ->
            val parts = key.split(":", limit = 2)
            val sourceHeader = parts.first()
            val targetKey = if (parts.size > 1) parts[1] else sourceHeader

            val node = fieldMap[sourceHeader]

            when (node) {
                is ExportNode.ExportNestedList<T, *> -> {
                    val innerTemplate = (element as? JsonObject)?.get($$"$schema")
                    if (innerTemplate != null) {
                        resultMap[targetKey] = processNestedList(node, entity, innerTemplate)
                    } else {
                        resultMap[targetKey] = JsonArray(emptyList())
                    }
                }
                is ExportNode.ExportNestedEntity<T, *> -> {
                    resultMap[targetKey] = processNestedEntity(node, entity, element)
                }
                else -> {
                    val finalKey = if (node != null) targetKey else key
                    resultMap[finalKey] = processElement(entity, element)
                }
            }
        }
        return JsonObject(resultMap)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <C> processNestedList(node: ExportNode.ExportNestedList<T, C>, parent: T, template: JsonElement): JsonArray {
        val list = node.property.get(parent)
        return JsonArray(list.map { node.schema.processInternal(it, template) })
    }

    @Suppress("UNCHECKED_CAST")
    private fun <C> processNestedEntity(node: ExportNode.ExportNestedEntity<T, C>, parent: T, template: JsonElement): JsonElement {
        val child = node.property.get(parent) ?: return JsonNull
        return node.schema.processInternal(child, template)
    }

    fun processInternal(entity: T, template: JsonElement): JsonElement = processElement(entity, template)

    private fun replaceTokens(template: String, entity: T): String {
        var res = template
        simpleFields.forEach { f ->
            // Optimización: solo reemplaza si contiene el token
            if (res.contains("@${f.header}")) res = res.replace("@${f.header}", f.getValue(entity))
        }
        return res
    }
}