package com.habitiora.linkarium.data.exporters

import com.habitiora.linkarium.data.local.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteDataUserManager @Inject constructor(
    private val db: AppDatabase
){
    fun delete(){
        CoroutineScope(Dispatchers.IO).launch {
            db.clearAllTables()
        }
    }
}