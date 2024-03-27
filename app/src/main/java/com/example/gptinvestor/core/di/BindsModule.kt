package com.example.gptinvestor.core.di

import com.example.gptinvestor.features.investor.data.repository.CompanyRepository
import com.example.gptinvestor.features.investor.domain.repository.ICompanyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {

    @Binds
    abstract fun providesCompanyRepository(repository: CompanyRepository): ICompanyRepository
}