package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.ClearCacheInteractor

class ClearMobileConfigsInteractor(
    repository: MobileConfigsRepository
) : ClearCacheInteractor<MobileConfigs>(repository)
