package com.kg.gettransfer.core.data

interface MutableDataSource<T> : ReadableDataSource<T>, WriteableDataSource<T>
