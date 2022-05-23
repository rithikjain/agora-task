package `in`.rithikjain.agoratask.di

import `in`.rithikjain.agoratask.R
import `in`.rithikjain.agoratask.agora.EngineEventListener
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
import io.agora.rtm.RtmCallManager
import io.agora.rtm.RtmClient
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

    @Singleton
    @Provides
    fun provideEngineEventListener() = EngineEventListener()

    @Singleton
    @Provides
    fun provideRTMClient(
        @ApplicationContext context: Context,
        engineEventListener: EngineEventListener
    ): RtmClient =
        RtmClient.createInstance(
            context,
            context.getString(R.string.agora_app_id),
            engineEventListener
        )

    @Singleton
    @Provides
    fun provideRTMCallManager(
        rtmClient: RtmClient,
        engineEventListener: EngineEventListener
    ): RtmCallManager {
        val callManager = rtmClient.rtmCallManager
        callManager.setEventListener(engineEventListener)
        return callManager
    }

}