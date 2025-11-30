package com.habitiora.linkarium.domain.usecase

import android.net.Uri
import com.habitiora.linkarium.domain.model.LinkEntry

data class LinkEntryImpl(
    override val id: Long = 0,
    override val seedId: Long = 0,
    override val order: Int = 0,
    override val uri: Uri,
    override val label: String? = null,
    override val note: String? = null
): LinkEntry{
    override fun equals(other: Any?): Boolean {
        return other is LinkEntryImpl &&
                seedId == other.seedId &&
                uri == other.uri
    }

    override fun hashCode(): Int {
        var result = seedId.hashCode()
        result = 31 * result + uri.hashCode()
        return result
    }
}