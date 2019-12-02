package com.kg.gettransfer.core.domain

open class ClearCacheInteractor<M>(
    protected val repository: ReadableRepository<M>
) {
    suspend operator fun invoke() = repository.clearCache()
}