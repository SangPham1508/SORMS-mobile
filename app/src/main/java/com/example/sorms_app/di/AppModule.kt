package com.example.sorms_app.di

import android.content.Context
import com.example.sorms_app.data.datasource.remote.AuthApiService
import com.example.sorms_app.data.datasource.remote.BookingApiService
import com.example.sorms_app.data.datasource.remote.FaceRecognitionApiService
import com.example.sorms_app.data.datasource.remote.NotificationApiService
import com.example.sorms_app.data.datasource.remote.OrderApiService
import com.example.sorms_app.data.datasource.remote.RetrofitClient
import com.example.sorms_app.data.datasource.remote.ServiceApiService
import com.example.sorms_app.data.datasource.remote.TaskApiService
import com.example.sorms_app.data.datasource.remote.UserApiService
import com.example.sorms_app.data.repository.ActivityRepositoryImpl
import com.example.sorms_app.data.repository.AuthRepository
import com.example.sorms_app.data.repository.BookingRepositoryImpl
import com.example.sorms_app.data.repository.NotificationRepositoryImpl
import com.example.sorms_app.data.repository.OrderRepositoryImpl
import com.example.sorms_app.data.repository.RoomRepository
import com.example.sorms_app.data.repository.RoomTypeRepository
import com.example.sorms_app.data.repository.ServiceRepositoryImpl
import com.example.sorms_app.data.repository.TaskRepositoryImpl
import com.example.sorms_app.data.repository.UserRepositoryImpl
import com.example.sorms_app.data.repository.StaffProfileRepositoryImpl
import com.example.sorms_app.domain.repository.ActivityRepository
import com.example.sorms_app.domain.repository.BookingRepository
import com.example.sorms_app.domain.repository.NotificationRepository
import com.example.sorms_app.domain.repository.OrderRepository
import com.example.sorms_app.domain.repository.ServiceRepository
import com.example.sorms_app.domain.repository.TaskRepository
import com.example.sorms_app.domain.repository.UserRepository
import com.example.sorms_app.domain.repository.StaffProfileRepository
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
    abstract fun bindOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindStaffProfileRepository(staffRepoImpl: StaffProfileRepositoryImpl): StaffProfileRepository
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
    fun provideRoomRepository(): RoomRepository {
        return RoomRepository()
    }

    @Provides
    @Singleton
    fun provideRoomTypeRepository(): RoomTypeRepository {
        return RoomTypeRepository()
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

    @Provides
    @Singleton
    fun provideOrderApiService(): OrderApiService = RetrofitClient.orderApiService

    @Provides
    @Singleton
    fun provideUserApiService(): UserApiService = RetrofitClient.userApiService

    @Provides
    @Singleton
    fun provideFaceRecognitionApiService(): FaceRecognitionApiService = RetrofitClient.faceRecognitionApiService
}
