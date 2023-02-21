package eu.epitech.reyditech.test

import eu.epitech.reyditech.Repository
import eu.epitech.reyditech.auth.LoginStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class InMemoryRepository(
    initialLoginStage: LoginStage = LoginStage.Unauthorized
) : Repository {
    private val _loginStage: MutableStateFlow<LoginStage> = MutableStateFlow(initialLoginStage)

    override fun loadLoginStage(): Flow<LoginStage> = _loginStage

    override suspend fun storeLoginStage(stage: LoginStage) {
        _loginStage.value = stage
    }
}
