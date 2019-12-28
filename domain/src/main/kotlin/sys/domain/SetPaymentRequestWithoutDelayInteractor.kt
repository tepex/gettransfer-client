package sys.domain

import com.kg.gettransfer.sys.domain.PreferencesRepository

class SetPaymentRequestWithoutDelayInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Boolean) {
        repository.getResult().getModel().copy(isPaymentRequestWithoutDelay = value).also { repository.put(it) }
    }
}