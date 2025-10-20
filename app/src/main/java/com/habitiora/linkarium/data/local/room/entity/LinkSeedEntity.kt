package com.habitiora.linkarium.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitiora.linkarium.data.local.room.DatabaseContract
import java.time.LocalDateTime

@Entity(
    tableName = DatabaseContract.LinkSeed.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = LinkGardenEntity::class,
            parentColumns = [DatabaseContract.LinkGarden.COLUMN_ID],
            childColumns = [DatabaseContract.LinkSeed.COLUMN_GARDEN_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index(value = [DatabaseContract.LinkSeed.COLUMN_NAME]),
        Index(value = [DatabaseContract.LinkSeed.COLUMN_GARDEN_ID]),
        Index(value = [DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE]),
        Index(value = [DatabaseContract.LinkSeed.COLUMN_DATE_TIME])
    ]
)
data class LinkSeedEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_ID) val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_NAME) val name: String,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_GARDEN_ID) val gardenId: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE) val isFavorite: Boolean = false,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_NOTES) val notes: String? = null,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_DATE_TIME) val modifiedAt: LocalDateTime = LocalDateTime.now()
)