package com.thejawnpaul.gptinvestor.features.company.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: CompanyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(companies: List<CompanyEntity>)

    @Query("SELECT * FROM company_table ORDER BY ticker")
    suspend fun getAllCompanies(): List<CompanyEntity>

    @Query("SELECT * FROM company_table ORDER BY ticker")
    fun getAllCompaniesFlow(): Flow<List<CompanyEntity>> // Stays as is

    @Query("SELECT * FROM company_table WHERE ticker =:ticker")
    suspend fun getCompany(ticker: String): CompanyEntity

    @Query("SELECT * FROM company_table WHERE sectorKey =:sectorKey")
    suspend fun getCompaniesInSector(sectorKey: String): List<CompanyEntity>

    @Query("SELECT * FROM company_table WHERE ticker IN (:ids)")
    suspend fun getCompaniesByTicker(ids: List<String>): List<CompanyEntity>

    @Query("SELECT * FROM company_table WHERE name LIKE '%' || :query || '%' OR ticker LIKE '%' || :query || '%'" )
    suspend fun searchAllCompanies(query: String): List<CompanyEntity> // Added suspend

    @Query("SELECT * FROM company_table WHERE sectorKey = :sectorKey AND name LIKE '%' || :query || '%' OR ticker LIKE '%' || :query || '%'" )
    suspend fun searchCompaniesInSector(query: String, sectorKey: String): List<CompanyEntity> // Added suspend

    @Update
    suspend fun updateCompanies(companies: List<CompanyEntity>)

    @Update
    suspend fun updateCompany(companies: CompanyEntity)
}
