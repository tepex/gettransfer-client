package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.ClearCacheInteractor
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ClearMobileConfigsInteractor(
    repository: MobileConfigsRepository
) : ClearCacheInteractor<MobileConfigs>(repository)
