package com.kg.gettransfer.core.domain

open class GetModelInteractor<M>(
    protected val repository: ReadableRepository<M>
) {
    suspend operator fun invoke() = repository.getResult()
}
