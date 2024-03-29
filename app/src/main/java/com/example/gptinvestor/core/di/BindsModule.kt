package com.example.gptinvestor.core.di

import com.example.gptinvestor.features.company.data.repository.CompanyRepository
import com.example.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.example.gptinvestor.features.investor.data.repository.InvestorRepository
import com.example.gptinvestor.features.investor.domain.repository.IInvestorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {

    @Binds
    abstract fun providesCompanyRepository(repository: CompanyRepository): ICompanyRepository

    @Binds
    abstract fun providesInvestorRepository(repository: InvestorRepository): IInvestorRepository
}
