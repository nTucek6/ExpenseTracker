package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.expensetracker.data.entity.Categories
import com.example.expensetracker.data.model.ExpenseWithGroupSum
import com.example.expensetracker.data.model.ManageCategories

@Dao
interface CategoriesDao {

    @Insert
    suspend fun insert(categories: Categories)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<Categories>)

    @Transaction
    suspend fun replaceAll(categories: List<Categories>) {
        deleteAll()
        insertAll(categories)
    }

    @Update
    suspend fun update(categories: Categories): Int

    @Delete
    suspend fun delete(categories: Categories): Int

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM categories where id = :id")
    suspend fun findById(id: String): Categories

    @Query("SELECT * FROM categories ")
    fun getAllCategories(): LiveData<List<Categories>>


    @Query(
        """
        SELECT c.*, COUNT(e.categoryId) AS expensesCount FROM categories c LEFT JOIN expenses e on c.id = e.categoryId GROUP BY c.id ORDER BY id ASC
    """
    )
    fun getCategoriesPaging(): PagingSource<Int, ManageCategories>

}