package com.habitiora.linkarium.data.local.room.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkEntry

@Entity(
    tableName = DatabaseContract.LinkEntry.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = LinkSeedEntity::class,
            parentColumns = [DatabaseContract.LinkSeed.COLUMN_ID],
            childColumns = [DatabaseContract.LinkEntry.COLUMN_SEED_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [DatabaseContract.LinkEntry.COLUMN_SEED_ID]),
        Index(value = [DatabaseContract.LinkEntry.COLUMN_LABEL])
    ]
)
data class LinkEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.LinkEntry.COLUMN_ID)
    override val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkEntry.COLUMN_SEED_ID)
    override val seedId: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkEntry.COLUMN_URI)
    override val uri: Uri,
    @ColumnInfo(name = DatabaseContract.LinkEntry.COLUMN_LABEL)
    override val label: String? = null,
    @ColumnInfo(name = DatabaseContract.LinkEntry.COLUMN_NOTE)
    override val note: String? = null
): LinkEntry
