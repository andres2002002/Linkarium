package com.habitiora.linkarium.data.local.room.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitiora.linkarium.data.local.room.DatabaseContract
import java.time.LocalDateTime
import com.habitiora.linkarium.domain.model.LinkSeed

@Entity(
    tableName = DatabaseContract.LinkSeed.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = LinkGardenEntity::class,
            parentColumns = [DatabaseContract.LinkGarden.COLUMN_ID],
            childColumns = [DatabaseContract.LinkSeed.COLUMN_COLLECTION],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index(value = [DatabaseContract.LinkSeed.COLUMN_NAME]),
        Index(value = [DatabaseContract.LinkSeed.COLUMN_COLLECTION]),
        Index(value = [DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE]),
        Index(value = [DatabaseContract.LinkSeed.COLUMN_DATE_TIME])
    ]
)
data class LinkSeedEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_ID)
    override val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_NAME)
    override val name: String,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_LINKS)
    override val links: List<Uri>,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_COLLECTION)
    override val collection: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE)
    override val isFavorite: Boolean = false,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_NOTES)
    override val notes: String? = null,
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_TAGS)
    override val tags: List<String> = emptyList(),
    @ColumnInfo(name = DatabaseContract.LinkSeed.COLUMN_DATE_TIME)
    override val modifiedAt: LocalDateTime = LocalDateTime.now()
) : LinkSeed {
    override fun update(
        id: Long,
        name: String,
        links: List<Uri>,
        collection: Long,
        isFavorite: Boolean,
        notes: String?,
        tags: List<String>,
        modifiedAt: LocalDateTime
    ): LinkSeed = LinkSeedEntity(
        id = id,
        name = name,
        links = links,
        collection = collection,
        isFavorite = isFavorite,
        notes = notes,
        tags = tags,
        modifiedAt = modifiedAt
    )
}