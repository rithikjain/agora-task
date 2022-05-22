package `in`.rithikjain.agoratask.di

import `in`.rithikjain.agoratask.repository.UserRepository
import `in`.rithikjain.agoratask.utils.PrefHelper
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val database: DatabaseReference = Firebase.database.reference

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        PrefHelper.customPrefs(context, "agora_task")

    @Singleton
    @Provides
    fun provideUserRepo(sharedPref: SharedPreferences): UserRepository =
        UserRepository(database, sharedPref)

}