package com.habitiora.linkarium.data.local.room

import android.net.Uri
import androidx.room.TypeConverter
import java.time.LocalDateTime
import androidx.core.net.toUri

class Converters {
    // --- Uri ---
    @TypeConverter
    fun fromUri(uri: Uri?): String? =
        uri?.toString()

    @TypeConverter
    fun toUri(data: String?): Uri? =
        data?.toUri()

    // --- LocalDateTime ---
    @TypeConverter
    fun fromDate(date: LocalDateTime?): String? =
        date?.toString()

    @TypeConverter
    fun toDate(date: String?): LocalDateTime? =
        date?.let { LocalDateTime.parse(it) }
}
