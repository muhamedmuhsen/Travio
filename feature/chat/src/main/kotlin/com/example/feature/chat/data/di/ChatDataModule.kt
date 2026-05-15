package com.example.feature.chat.data.di

import com.example.feature.chat.data.remote.SignalRService
import com.example.feature.chat.data.remote.SignalRServiceImpl
import com.example.feature.chat.data.repository.SignalRChatRepositoryImpl
import com.example.feature.chat.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(signalRChatRepositoryImpl: SignalRChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindSignalRService(signalRServiceImpl: SignalRServiceImpl): SignalRService
}
