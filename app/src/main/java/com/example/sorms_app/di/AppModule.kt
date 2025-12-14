package com.example.sorms_app.di

import android.content.Context
import com.example.sorms_app.data.datasource.remote.AuthApiService
import com.example.sorms_app.data.datasource.remote.BookingApiService
import com.example.sorms_app.data.datasource.remote.NotificationApiService
import com.example.sorms_app.data.datasource.remote.RetrofitClient
import com.example.sorms_app.data.datasource.remote.ServiceApiService
import com.example.sorms_app.data.datasource.remote.TaskApiService
import com.example.sorms_app.data.repository.*
import com.example.sorms_app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(bookingRepositoryImpl: BookingRepositoryImpl): BookingRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(serviceRepositoryImpl: ServiceRepositoryImpl): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(activityRepositoryImpl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(scheduleRepositoryImpl: ScheduleRepositoryImpl): ScheduleRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepository(context) // AuthRepository has a dependency on Context
    }

    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService = RetrofitClient.authApiService

    @Provides
    @Singleton
    fun provideBookingApiService(): BookingApiService = RetrofitClient.bookingApiService

    @Provides
    @Singleton
    fun provideNotificationApiService(): NotificationApiService = RetrofitClient.notificationApiService

    @Provides
    @Singleton
    fun provideServiceApiService(): ServiceApiService = RetrofitClient.serviceApiService

    @Provides
    @Singleton
    fun provideTaskApiService(): TaskApiService = RetrofitClient.taskApiService
}
