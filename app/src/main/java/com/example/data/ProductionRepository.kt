package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductionRepository(private val db: ProductionDatabase) {
    private val userDao = db.userDao()
    private val productionRecordDao = db.productionRecordDao()
    private val closingRecordDao = db.closingRecordDao()

    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allProductionRecords: Flow<List<ProductionRecord>> = productionRecordDao.getAllRecords()
    val allClosingRecords: Flow<List<ClosingRecord>> = closingRecordDao.getAllClosings()

    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByUsername(username)
    }

    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        userDao.deleteUser(user)
    }

    fun getProductionByDate(date: String): Flow<List<ProductionRecord>> {
        return productionRecordDao.getRecordsByDate(date)
    }

    suspend fun insertProductionRecord(record: ProductionRecord): Long = withContext(Dispatchers.IO) {
        productionRecordDao.insertRecord(record)
    }

    suspend fun updateProductionRecord(record: ProductionRecord) = withContext(Dispatchers.IO) {
        productionRecordDao.updateRecord(record)
    }

    suspend fun deleteProductionRecord(record: ProductionRecord) = withContext(Dispatchers.IO) {
        productionRecordDao.deleteRecord(record)
    }

    fun getClosingByDate(date: String): Flow<ClosingRecord?> {
        return closingRecordDao.getClosingByDate(date)
    }

    suspend fun insertClosingRecord(closing: ClosingRecord): Long = withContext(Dispatchers.IO) {
        closingRecordDao.insertClosing(closing)
    }

    suspend fun updateClosingRecord(closing: ClosingRecord) = withContext(Dispatchers.IO) {
        closingRecordDao.updateClosing(closing)
    }

    suspend fun deleteClosingRecord(closing: ClosingRecord) = withContext(Dispatchers.IO) {
        closingRecordDao.deleteClosing(closing)
    }

    suspend fun prepopulateDefaultUsers() = withContext(Dispatchers.IO) {
        // Double check we have at least one user
        val adminUser = userDao.getUserByUsername("admin")
        if (adminUser == null) {
            userDao.insertUser(User(username = "admin", password = "123", role = "Administrador"))
        }
        val normalUser = userDao.getUserByUsername("user")
        if (normalUser == null) {
            userDao.insertUser(User(username = "user", password = "123", role = "Funcionário"))
        }
    }
}
