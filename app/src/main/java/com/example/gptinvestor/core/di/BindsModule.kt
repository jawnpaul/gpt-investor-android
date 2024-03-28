package com.example.gptinvestor.core.di

import com.example.gptinvestor.features.company.data.repository.CompanyRepository
import com.example.gptinvestor.features.company.domain.repository.ICompanyRepository
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
