package com.saxpenguin.umamusumefuturesight.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Data layer module for Hilt dependency injection.
 * Provides repositories and data source dependencies.
 * 
 * Note: BannerRepository is automatically provided by Hilt via its @Inject constructor.
 * Additional bindings can be added here when interfaces are introduced.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // BannerRepository is auto-provided via @Inject constructor
    // Add @Binds methods here when using interfaces (e.g., Repository -> RepositoryImpl)
}
