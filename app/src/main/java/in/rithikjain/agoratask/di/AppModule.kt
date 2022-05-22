package `in`.rithikjain.agoratask.di

import `in`.rithikjain.agoratask.repository.UserRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val database: DatabaseReference = Firebase.database.reference

    @Singleton
    @Provides
    fun provideUserRepo(): UserRepository = UserRepository(database)
}