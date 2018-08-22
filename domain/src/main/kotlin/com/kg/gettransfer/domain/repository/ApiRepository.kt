package com.kg.gettransfer.domain.repository

interface ApiRepository {
	suspend fun updateToken(): String
	suspend fun configs()
}
