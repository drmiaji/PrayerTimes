package com.drmiaji.prayertimes.repo.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drmiaji.prayertimes.repo.local.entity.CheckedTaskEntity
import com.drmiaji.prayertimes.repo.local.entity.ProgressTaskEntity
import com.drmiaji.prayertimes.repo.local.entity.ReminderEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [ReminderEntity::class, ProgressTaskEntity::class, CheckedTaskEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AlifDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var instance: AlifDatabase? = null
        @OptIn(DelicateCoroutinesApi::class)
        fun getInstance(context: Context): AlifDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlifDatabase::class.java, "Alif.db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        instance?.let {
                            GlobalScope.launch {
                                it.reminderDao().addAllReminder(ReminderEntity.INIT)
                            }
                        }
                    }
                }).fallbackToDestructiveMigration().build()
            }
    }
}