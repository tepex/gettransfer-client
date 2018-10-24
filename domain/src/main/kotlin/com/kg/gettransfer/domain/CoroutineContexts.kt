package com.kg.gettransfer.domain

import kotlinx.coroutines.CoroutineDispatcher

class CoroutineContexts(val ui: CoroutineDispatcher, val bg: CoroutineDispatcher)
