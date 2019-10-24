package com.kg.gettransfer.core.domain

interface MutableRepository<T> : ReadableRepository<T>, WriteableRepository<T>
