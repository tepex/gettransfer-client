package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferRepositoryImpl(private val apiRepository: ApiRepositoryImpl): OfferRepository {
    override suspend fun getOffers(id: Long) = apiRepository.getOffers(id)
}
