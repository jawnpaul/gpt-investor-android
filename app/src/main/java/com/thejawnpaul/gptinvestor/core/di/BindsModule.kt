package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepositoryImpl
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.conversation.data.repository.ConversationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepositoryImpl
import com.thejawnpaul.gptinvestor.features.history.data.repository.HistoryRepository
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import com.thejawnpaul.gptinvestor.features.investor.data.repository.InvestorRepository
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepositoryImpl
import com.thejawnpaul.gptinvestor.features.toppick.data.repository.TopPickRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
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

    @Binds
    abstract fun providesHistoryRepository(repository: HistoryRepository): IHistoryRepository

    @Binds
    abstract fun providesTopPicksRepository(repository: TopPickRepository): ITopPickRepository

    @Binds
    abstract fun providesAuthRepository(repository: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    abstract fun providesFeedbackRepository(repository: FeedbackRepositoryImpl): FeedbackRepository

    @Binds
    abstract fun providesNotificationRepository(repository: NotificationRepositoryImpl): NotificationRepository
}
