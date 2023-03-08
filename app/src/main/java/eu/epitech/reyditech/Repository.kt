package eu.epitech.reyditech

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import eu.epitech.reyditech.auth.LoginStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "repository")

/**
 * Persistent data storage.
 *
 * All operations in this class are asynchronous because they involve disk reads/writes.
 */
internal interface Repository {
    fun loadLoginStage(): Flow<LoginStage>

    suspend fun storeLoginStage(stage: LoginStage)
}

internal fun Repository(application: Application): Repository = DataStoreRepository(application)

private class DataStoreRepository(private val application: Application) : Repository {
    private val context: Context
        get() = application.applicationContext

    companion object {
        private val LOGIN_STAGE_KEY = stringPreferencesKey("login_stage")
    }

    override fun loadLoginStage(): Flow<LoginStage> =
        context.dataStore.data.map { prefs -> LoginStage.fromJson(prefs[LOGIN_STAGE_KEY]) }

    override suspend fun storeLoginStage(stage: LoginStage) {
        context.dataStore.edit { prefs -> prefs[LOGIN_STAGE_KEY] = stage.toJson() }
    }
}
