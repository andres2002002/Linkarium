package com.habitiora.linkarium.core

// Archivo: com/example/myapp/util/UriTypeAdapter.kt (o donde prefieras)

import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import androidx.core.net.toUri

class UriTypeAdapter : TypeAdapter<Uri>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Uri?) {
        // Serialización: De objeto Uri a String en JSON
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Uri? {
        // Deserialización: De String en JSON a objeto Uri
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return reader.nextString().toUri()
    }
}