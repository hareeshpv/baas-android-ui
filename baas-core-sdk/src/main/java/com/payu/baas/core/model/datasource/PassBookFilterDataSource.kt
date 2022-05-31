@file:JvmSynthetic

package com.payu.baas.core.model.datasource

import com.payu.baas.core.model.entities.model.TypeModel

internal object PassBookFilterDataSource {

    val ALL_TIME = "All Time"
    val LAST_MONTH = "Last 30 days"
    val LAST_3_MONTHS = "Last 90 days"
    val LAST_1_YEAR = "Last 1 Year"
    val CUSTOM_TIME_PERIOD = "Custom"
    val ALL = "All"

    val timePeriodTypes: ArrayList<TypeModel>
        get() {
            return arrayListOf(
                TypeModel(
                    ALL_TIME,
                    ALL_TIME,
                    false
                ), TypeModel(
                    LAST_MONTH,
                    LAST_MONTH,
                    false
                ), TypeModel(
                    LAST_3_MONTHS,
                    LAST_3_MONTHS,
                    false
                ), TypeModel(
                    LAST_1_YEAR,
                    LAST_1_YEAR,
                    false
                ), TypeModel(
                    CUSTOM_TIME_PERIOD,
                    CUSTOM_TIME_PERIOD,
                    false
                )
            )
        }

    val transactionTypes: ArrayList<TypeModel>
        get() {
            return arrayListOf(
                TypeModel(
                    ALL,
                    ALL,
                    false
                ), TypeModel(
                    "Received",
                    "Credit",
                    false
                ), TypeModel(
                    "Paid",
                    "Debit",
                    false
                )
            )
        }

    val accountTypes: ArrayList<TypeModel>
        get() {
            return arrayListOf(
                TypeModel(
                    ALL,
                    ALL,
                    false
                ), TypeModel(
                    "Salary Account",
                    "Prepaid_Account",
                    false
                ), TypeModel(
                    "Advanced Account",
                    "Limit_Account",
                    false
                )
            )
        }
}