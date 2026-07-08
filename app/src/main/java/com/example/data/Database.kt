package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface ProductionRecordDao {
    @Query("SELECT * FROM production_records ORDER BY date DESC, timestamp DESC")
    fun getAllRecords(): Flow<List<ProductionRecord>>

    @Query("SELECT * FROM production_records WHERE date = :date")
    fun getRecordsByDate(date: String): Flow<List<ProductionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ProductionRecord): Long

    @Update
    suspend fun updateRecord(record: ProductionRecord)

    @Delete
    suspend fun deleteRecord(record: ProductionRecord)
}

@Dao
interface ClosingRecordDao {
    @Query("SELECT * FROM closing_records ORDER BY date DESC, timestamp DESC")
    fun getAllClosings(): Flow<List<ClosingRecord>>

    @Query("SELECT * FROM closing_records WHERE date = :date LIMIT 1")
    fun getClosingByDate(date: String): Flow<ClosingRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClosing(closing: ClosingRecord): Long

    @Update
    suspend fun updateClosing(closing: ClosingRecord)

    @Delete
    suspend fun deleteClosing(closing: ClosingRecord)
}

@Database(entities = [User::class, ProductionRecord::class, ClosingRecord::class], version = 2, exportSchema = false)
abstract class ProductionDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productionRecordDao(): ProductionRecordDao
    abstract fun closingRecordDao(): ClosingRecordDao
}
