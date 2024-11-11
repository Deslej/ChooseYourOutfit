package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.CategoryDao
import com.example.chooseyouroutfit.data.entities.Category

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }
    suspend fun getCategoryById(id: Long): Category{
        return categoryDao.getCategoryById(id)
    }
}