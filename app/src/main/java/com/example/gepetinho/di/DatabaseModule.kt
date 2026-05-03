package com.example.gepetinho.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gepetinho.data.local.GepetinhoDatabase
import com.example.gepetinho.data.local.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "gepetinho.db"

private val MIGRATION_1_3 = object : Migration(1, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pokemon_new (
                pokemonId INTEGER NOT NULL,
                name TEXT NOT NULL,
                imageUrl TEXT,
                PRIMARY KEY(pokemonId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO pokemon_new (pokemonId, name, imageUrl)
            SELECT pokemonId, name, imageUrl FROM pokemon
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favorite_pokemon (
                userId TEXT NOT NULL,
                pokemonId INTEGER NOT NULL,
                PRIMARY KEY(userId, pokemonId)
            )
            """.trimIndent()
        )
        db.execSQL("DROP TABLE pokemon")
        db.execSQL("ALTER TABLE pokemon_new RENAME TO pokemon")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pokemon_details_new (
                pokemonId INTEGER NOT NULL,
                name TEXT NOT NULL,
                imageUrl TEXT,
                height INTEGER NOT NULL,
                weight INTEGER NOT NULL,
                baseExperience INTEGER,
                types TEXT NOT NULL,
                PRIMARY KEY(pokemonId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO pokemon_details_new (
                pokemonId,
                name,
                imageUrl,
                height,
                weight,
                baseExperience,
                types
            )
            SELECT pokemonId, name, imageUrl, height, weight, baseExperience, types
            FROM pokemon_details
            """.trimIndent()
        )
        db.execSQL("DROP TABLE pokemon_details")
        db.execSQL("ALTER TABLE pokemon_details_new RENAME TO pokemon_details")
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pokemon_new (
                pokemonId INTEGER NOT NULL,
                name TEXT NOT NULL,
                imageUrl TEXT,
                PRIMARY KEY(pokemonId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR IGNORE INTO pokemon_new (pokemonId, name, imageUrl)
            SELECT pokemonId, name, imageUrl FROM pokemon
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pokemon_details_new (
                pokemonId INTEGER NOT NULL,
                name TEXT NOT NULL,
                imageUrl TEXT,
                height INTEGER NOT NULL,
                weight INTEGER NOT NULL,
                baseExperience INTEGER,
                types TEXT NOT NULL,
                PRIMARY KEY(pokemonId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR IGNORE INTO pokemon_details_new (
                pokemonId,
                name,
                imageUrl,
                height,
                weight,
                baseExperience,
                types
            )
            SELECT pokemonId, name, imageUrl, height, weight, baseExperience, types
            FROM pokemon_details
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favorite_pokemon (
                userId TEXT NOT NULL,
                pokemonId INTEGER NOT NULL,
                PRIMARY KEY(userId, pokemonId)
            )
            """.trimIndent()
        )

        db.execSQL("DROP TABLE pokemon")
        db.execSQL("ALTER TABLE pokemon_new RENAME TO pokemon")
        db.execSQL("DROP TABLE pokemon_details")
        db.execSQL("ALTER TABLE pokemon_details_new RENAME TO pokemon_details")
    }
}

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
        )
            .addMigrations(MIGRATION_1_3, MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonDao(
        database: GepetinhoDatabase
    ): PokemonDao = database.pokemonDao()
}
