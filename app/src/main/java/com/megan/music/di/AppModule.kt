package com.megan.music.di

import com.megan.music.data.api.MeganApi
import com.megan.music.data.api.MusicDiscoveryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("megan")
    fun provideMeganRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(MeganApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideMeganApi(@Named("megan") retrofit: Retrofit): MeganApi =
        retrofit.create(MeganApi::class.java)

    @Provides
    @Singleton
    @Named("discovery")
    fun provideDiscoveryRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(MusicDiscoveryApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideDiscoveryApi(@Named("discovery") retrofit: Retrofit): MusicDiscoveryApi =
        retrofit.create(MusicDiscoveryApi::class.java)
}
