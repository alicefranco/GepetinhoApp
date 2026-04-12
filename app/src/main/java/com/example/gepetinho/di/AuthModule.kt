package com.example.gepetinho.di

import com.example.gepetinho.data.repository.FakeAuthRepository
import com.example.gepetinho.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        fakeAuthRepository: FakeAuthRepository
    ): AuthRepository
}
