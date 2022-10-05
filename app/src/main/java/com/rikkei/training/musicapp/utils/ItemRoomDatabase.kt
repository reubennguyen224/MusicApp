package com.rikkei.training.musicapp.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rikkei.training.musicapp.model.AlbumItem
import com.rikkei.training.musicapp.model.Artist
import com.rikkei.training.musicapp.model.Song

@Database(entities = [Song::class, AlbumItem::class, Artist::class], //list entities in schema
    version = 2, //version of schema, when change schema ++version
    exportSchema = false// don't give a cope of schema
)
abstract class ItemRoomDatabase: RoomDatabase() {
    abstract fun itemDao(): RoomDAO
    companion object{
        @Volatile
        private var instance: ItemRoomDatabase? = null
        fun getDatabase(context: Context): ItemRoomDatabase{
            return instance ?: synchronized(this){
                val instances = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "temp_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                instance = instances
                return instances
            }
        }
    }
}