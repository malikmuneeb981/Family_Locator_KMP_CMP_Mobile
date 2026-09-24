package org.example.project.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.example.project.data.local.AppDatabase
import org.example.project.data.local.ConnectedFriendsDao
import org.example.project.data.local.ConnectedFriendsRepoImpl
import org.example.project.data.remote.LocationService
import org.example.project.data.remote.LocationServiceImpl
import org.example.project.data.remote.UserRepoImpl
import org.example.project.data.remote.UserService
import org.example.project.domain.repostories.local.ConnectedFriendsRepo
import org.example.project.domain.useCases.remote.CreateProfileUseCases
import org.example.project.domain.repostories.remote.LocationRepo
import org.example.project.domain.repostories.remote.UserRepo
import org.example.project.domain.useCases.local.AddFriendToRoomUseCase
import org.example.project.domain.useCases.local.DeleteFriendFromRoomUseCase
import org.example.project.domain.useCases.local.GetAllFriendsFromRoomUseCase
import org.example.project.domain.useCases.remote.ConnectToUsersWithWebSocketsUseCase
import org.example.project.domain.useCases.remote.ConnectUsersUseCase
import org.example.project.domain.useCases.remote.DisconnectUserFromWebSocketsUseCase
import org.example.project.domain.useCases.remote.LocationUseCases
import org.example.project.domain.useCases.remote.ObserveAllFriendsLatLongsUseCase
import org.example.project.domain.useCases.remote.ObserveSessionMessagesUseCase
import org.example.project.domain.useCases.remote.SendLocationUseCase
import org.example.project.domain.useCases.remote.SignAuthUseCase
import org.example.project.domain.useCases.remote.SignUpUserUseCase
import org.example.project.presentation.screens.realtimetracker.SendRealTimeLocationForService
import org.example.project.utils.DataStorePreferences
import org.koin.dsl.module

val sharedModule = module {

    //User
    single<UserService> {
        UserService(get())
    }
    single<UserRepo> {
        UserRepoImpl(get())
    }
    single<SignUpUserUseCase> {
        SignUpUserUseCase(get())
    }
    single<SignAuthUseCase> {
        SignAuthUseCase(get())
    }
    single<CreateProfileUseCases> {
        CreateProfileUseCases(get(),get())
    }
    single<ConnectUsersUseCase> {
        ConnectUsersUseCase(get())
    }

    //Location
    single<LocationService> {
        LocationService(get())
    }
    single<LocationRepo> {
        LocationServiceImpl(get())
    }
    single<ConnectToUsersWithWebSocketsUseCase> {
        ConnectToUsersWithWebSocketsUseCase(get())
    }
    single<SendLocationUseCase> {
        SendLocationUseCase(get())
    }
    single<ObserveAllFriendsLatLongsUseCase> {
        ObserveAllFriendsLatLongsUseCase(get())
    }
    single<ObserveSessionMessagesUseCase> {
        ObserveSessionMessagesUseCase(get())
    }
    single<LocationUseCases> {
        LocationUseCases(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<DisconnectUserFromWebSocketsUseCase> {
        DisconnectUserFromWebSocketsUseCase(
            get(),
        )
    }


    //Others
    single<DataStorePreferences> {
        DataStorePreferences(get())
    }
    single<SendRealTimeLocationForService> {
        SendRealTimeLocationForService(get(),get(),get())
    }
    single<AppDatabase>{
        get<RoomDatabase.Builder<AppDatabase>>().setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }
    single<ConnectedFriendsDao>{
        get<AppDatabase>().connectedFriendsDao()
    }
    single<ConnectedFriendsRepo>{
        ConnectedFriendsRepoImpl(get<ConnectedFriendsDao>())
    }
    single<AddFriendToRoomUseCase>{
        AddFriendToRoomUseCase(get<ConnectedFriendsRepo>())
    }
    single<GetAllFriendsFromRoomUseCase>{
        GetAllFriendsFromRoomUseCase(get<ConnectedFriendsRepo>())
    }
    single<DeleteFriendFromRoomUseCase> {
        DeleteFriendFromRoomUseCase(get())
    }
}