package com.kg.gettransfer.sys.domain

import sys.domain.AccessTokenRepository

class SetAccessTokenInteractor(
    private val accessTokenRepository: AccessTokenRepository
) {

    suspend operator fun invoke(value: String) {
        accessTokenRepository.put(value)
    }
}
