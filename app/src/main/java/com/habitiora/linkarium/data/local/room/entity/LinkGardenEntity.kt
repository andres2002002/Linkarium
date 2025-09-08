package com.habitiora.linkarium.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkGarden

@Entity(
    tableName = DatabaseContract.LinkGarden.TABLE_NAME,
    indices = [androidx.room.Index(DatabaseContract.LinkGarden.COLUMN_NAME)]
)
data class LinkGardenEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.LinkGarden.COLUMN_ID)
    override val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.LinkGarden.COLUMN_NAME)
    override val name: String,
    @ColumnInfo(name = DatabaseContract.LinkGarden.COLUMN_DESCRIPTION)
    override val description: String = "",
): LinkGarden{
    override fun update(
        id: Long,
        name: String,
        description: String
    ): LinkGarden = LinkGardenEntity(
        id = id,
        name = name,
        description = description
    )
}
