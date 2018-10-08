package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.OffersRepository

class OffersInteractor(private val repository: OffersRepository) {
    suspend fun getOffers(id: Long) = repository.getOffers(id)
}
