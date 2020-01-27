package sys.data

import com.kg.gettransfer.data.PreferencesCache
import org.koin.core.KoinComponent
import org.koin.core.inject
import sys.domain.AccessTokenRepository

class AccessTokenRepositoryImpl : AccessTokenRepository, KoinComponent {

    private val preferencesCache: PreferencesCache by inject()

    override suspend fun put(value: String) {
        preferencesCache.accessToken = value
    }
}