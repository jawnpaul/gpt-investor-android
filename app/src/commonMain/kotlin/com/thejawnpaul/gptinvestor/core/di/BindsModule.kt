package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepositoryImpl
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.features.conversation.data.repository.ConversationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepositoryImpl
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepositoryImpl
import com.thejawnpaul.gptinvestor.features.history.data.repository.HistoryRepository
import com.thejawnpaul.gptinvestor.features.history.domain.repository.IHistoryRepository
import com.thejawnpaul.gptinvestor.features.investor.data.repository.InvestorRepository
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepositoryImpl
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepositoryImpl
import com.thejawnpaul.gptinvestor.features.toppick.data.repository.TopPickRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


val bindingModule = module {

    factoryOf(::CompanyRepository) bind ICompanyRepository::class

    factoryOf(::InvestorRepository) bind IInvestorRepository::class

    factoryOf(::ConversationRepository) bind IConversationRepository::class

    factoryOf(::HistoryRepository) bind IHistoryRepository::class

    factoryOf(::TopPickRepository) bind ITopPickRepository::class

    factoryOf(::AuthenticationRepositoryImpl) bind AuthenticationRepository::class

    factoryOf(::FeedbackRepositoryImpl) bind FeedbackRepository::class

    factoryOf(::NotificationRepositoryImpl) bind NotificationRepository::class

    factoryOf(::ModelsRepositoryImpl) bind ModelsRepository::class

    factoryOf(::TidbitRepositoryImpl) bind TidbitRepository::class
}
