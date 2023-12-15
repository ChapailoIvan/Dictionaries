package by.chapailo.dictionaries.di

import android.content.Context
import by.chapailo.dictionaries.data.DatabaseManagerImpl
import by.chapailo.dictionaries.data.SharedPreferencesManagerImpl
import by.chapailo.dictionaries.domain.DatabaseManager
import by.chapailo.dictionaries.domain.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabaseManager(
        @ApplicationContext appContext: Context
    ): DatabaseManager {
        return DatabaseManagerImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(
        @ApplicationContext appContext: Context
    ): SharedPreferencesManager {
        return SharedPreferencesManagerImpl(appContext)
    }

}