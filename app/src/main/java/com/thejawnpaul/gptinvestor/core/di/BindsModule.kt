package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.conversation.data.repository.ConversationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.investor.data.repository.InvestorRepository
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
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

    @Binds
    abstract fun providesConversationRepository(repository: ConversationRepository): IConversationRepository
}
