package com.india.epilepsyfoundation

import android.content.Context
import com.india.epilepsyfoundation.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class HiltAppIOModule {

    @Provides
    fun provideAppDb(@ApplicationContext appContext: Context) : AppDatabase {
        return AppDatabase.getInstance(appContext)
    }

}