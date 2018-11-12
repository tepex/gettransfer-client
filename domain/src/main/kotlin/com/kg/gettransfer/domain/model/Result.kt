package com.kg.gettransfer.domain.model

import com.kg.gettransfer.domain.ApiException

class Result<M>(val model: M? = null, val error: ApiException? = null)
