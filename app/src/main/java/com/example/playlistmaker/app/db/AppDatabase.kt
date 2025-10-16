package com.example.playlistmaker.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.app.db.dao.TrackDao


@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}