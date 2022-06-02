package com.payu.baas.core.model

import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.params.ApiParams

data class ApiDetails(val apiName: ApiName, val apiParams: ApiParams)