package com.example.safetyandcheap.modules

import com.example.safetyandcheap.auth.TokenManager
import com.example.safetyandcheap.service.CurrentUserState
import com.example.safetyandcheap.service.MainApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideUserState(
        tokenManager: TokenManager,
        mainApi: MainApiService
    ): CurrentUserState {
        return CurrentUserState(tokenManager, mainApi)
    }
}