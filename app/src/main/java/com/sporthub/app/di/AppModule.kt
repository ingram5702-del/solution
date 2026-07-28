package com.sporthub.app.di

import android.content.Context
import androidx.room.Room
import com.sporthub.app.data.AppDatabase
import com.sporthub.app.data.NoteDao
import com.sporthub.app.data.SportEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "sporthub.db").build()

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideSportEventDao(database: AppDatabase): SportEventDao = database.sportEventDao()
}
