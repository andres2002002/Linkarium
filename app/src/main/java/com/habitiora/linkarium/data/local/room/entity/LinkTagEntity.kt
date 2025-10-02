package com.habitiora.linkarium.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkTag

@Entity(
    tableName = DatabaseContract.LinkTag.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = LinkSeedEntity::class,
            parentColumns = [DatabaseContract.LinkSeed.COLUMN_ID],
            childColumns = [DatabaseContract.LinkTag.COLUMN_SEED_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [DatabaseContract.LinkTag.COLUMN_SEED_ID]),
        Index(value = [DatabaseContract.LinkTag.COLUMN_TAG])
    ]
)
data class LinkTagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.LinkTag.COLUMN_ID)
    override val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkTag.COLUMN_SEED_ID)
    override val seedId: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkTag.COLUMN_TAG)
    override val tag: String
): LinkTag
