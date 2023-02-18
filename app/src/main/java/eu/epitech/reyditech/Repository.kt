package eu.epitech.reyditech

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "repository")

/**
 * Persistent data storage.
 *
 * All operations in this class are asynchronous because they involve disk reads/writes.
 */
internal class Repository(application: Application) {
    private val context: Context = application.applicationContext

    companion object {
        val AUTH_STATE_KEY = stringPreferencesKey("auth_state")
    }

    fun loadRawAuthState(): Flow<String> =
        context.dataStore.data.map { prefs -> prefs[AUTH_STATE_KEY] ?: "{}" }

    suspend fun storeRawAuthState(rawState: String) {
        context.dataStore.edit { prefs -> prefs[AUTH_STATE_KEY] = rawState }
    }
}
