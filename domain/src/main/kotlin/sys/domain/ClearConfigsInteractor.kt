package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.ClearCacheInteractor

class ClearConfigsInteractor(repository: ConfigsRepository) : ClearCacheInteractor<Configs>(repository)