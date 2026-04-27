package com.example.gepetinho.di

import android.content.Context
import androidx.room.Room
import com.example.gepetinho.data.local.GepetinhoDatabase
import com.example.gepetinho.data.local.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "gepetinho.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GepetinhoDatabase {
        return Room.databaseBuilder(
            context,
            GepetinhoDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePokemonDao(
        database: GepetinhoDatabase
    ): PokemonDao = database.pokemonDao()
}
