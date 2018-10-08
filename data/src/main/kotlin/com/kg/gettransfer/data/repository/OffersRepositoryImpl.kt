package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.repository.OffersRepository

class OffersRepositoryImpl(private val apiRepository: ApiRepositoryImpl): OffersRepository {
    override suspend fun getOffers(id: Long) = apiRepository.getOffers(id)
}
