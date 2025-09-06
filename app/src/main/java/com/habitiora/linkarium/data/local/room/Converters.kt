package com.habitiora.linkarium.data.local.room

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import androidx.core.net.toUri

class Converters {

    private val gson = Gson()

    // --- Uri List ---
    @TypeConverter
    fun fromUriList(list: List<Uri>?): String? =
        gson.toJson(list?.map { it.toString() })

    @TypeConverter
    fun toUriList(data: String?): List<Uri> {
        if (data.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        val stringList: List<String> = gson.fromJson(data, type)
        return stringList.map { it.toUri() }
    }

    // --- String List (Tags) ---
    @TypeConverter
    fun fromTagList(list: List<String>?): String? =
        gson.toJson(list)

    @TypeConverter
    fun toTagList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, type)
    }

    // --- LocalDateTime ---
    @TypeConverter
    fun fromDate(date: LocalDateTime?): String? =
        date?.toString()

    @TypeConverter
    fun toDate(date: String?): LocalDateTime? =
        date?.let { LocalDateTime.parse(it) }
}
