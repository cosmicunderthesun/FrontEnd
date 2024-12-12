package com.example.caps.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.caps.database.AppDatabase
import com.example.caps.database.FavoriteMonument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteMonumentRepository(application: Application) {

    // DAO untuk mengakses database
    private val favoriteMonumentDao = AppDatabase.getDatabase(application).favoriteMonumentDao()

    /**
     * Mendapatkan semua data monumen favorit dari database.
     * Data akan diamati menggunakan LiveData.
     */
    fun getAllFavorites(): LiveData<List<FavoriteMonument>> {
        return favoriteMonumentDao.getAllFavorites()
    }

    /**
     * Menambahkan monumen ke daftar favorit.
     * Operasi ini berjalan di thread background (IO Dispatcher).
     */
    suspend fun addFavorite(favoriteMonument: FavoriteMonument) {
        favoriteMonumentDao.insert(favoriteMonument)
    }

    /**
     * Menghapus monumen dari daftar favorit.
     * Operasi ini berjalan di thread background (IO Dispatcher).
     */
    suspend fun removeFavorite(favoriteMonument: FavoriteMonument) {
        favoriteMonumentDao.delete(favoriteMonument)
    }
}
