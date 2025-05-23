package com.example.safetyandcheap.modules

import com.example.safetyandcheap.auth.TokenManager
import com.example.safetyandcheap.data.adapter.PropertyTypeAdapter
import com.example.safetyandcheap.service.AuthApiService
import com.example.safetyandcheap.data.network.converter.PlainTextConverterFactory
import com.example.safetyandcheap.data.network.incerceptor.BearerInterceptor
import com.example.safetyandcheap.data.network.incerceptor.ImgurAuthInterceptor
import com.example.safetyandcheap.service.MainApiService
import com.example.safetyandcheap.service.dto.property.Property
import com.example.safetyandcheap.service.dto.search.PropertySearchDto
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://192.168.0.105:8080/"

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(PlainTextConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("main")
    fun provideMainRetrofit(
        bearerInterceptor: BearerInterceptor
    ): Retrofit {
        val gson = GsonBuilder()
            .setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes): Boolean {
                    // Исключаем поля суперкласса с аннотацией @SerializedName
                    return (f.declaringClass == Property::class.java || f.declaringClass == PropertySearchDto::class.java) &&
                            f.getAnnotation(SerializedName::class.java) != null
                }

                override fun shouldSkipClass(clazz: Class<*>): Boolean = false
            })
            .registerTypeAdapter(Property::class.java, PropertyTypeAdapter())
            .create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(PlainTextConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(bearerInterceptor)
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@Named("auth") authRetrofit: Retrofit): AuthApiService {
        return authRetrofit.create(AuthApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideImgurAuthInterceptor(@Named("imgur_client_id") clientId: String): ImgurAuthInterceptor {
        return ImgurAuthInterceptor(clientId)
    }

    @Provides
    @Singleton
    fun providesMainApiService(@Named("main") retrofit: Retrofit): MainApiService {
        return retrofit.create(MainApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBearerInterceptor(
        tokenManager: TokenManager
    ): BearerInterceptor  {
        return BearerInterceptor(
            tokenManager
        )
    }
}