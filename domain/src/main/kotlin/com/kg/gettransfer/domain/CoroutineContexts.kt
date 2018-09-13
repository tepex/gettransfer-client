package com.kg.gettransfer.domain

import kotlinx.coroutines.experimental.CoroutineDispatcher

class CoroutineContexts(val ui: CoroutineDispatcher, val bg: CoroutineDispatcher)
