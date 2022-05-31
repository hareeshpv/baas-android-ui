package com.payu.baas.core.model.entities.model

data class TypeModel(
    val label: String,
    val code: String,
    var selected: Boolean = false
)
