package com.tastyhome.core.database.entity.test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrUpdateTestEntity(entity: TestEntity): Long

    @Query("SELECT * FROM TestEntity WHERE message = :message")
    fun observeTestEntities(message: String): Flow<List<TestEntity?>>
}