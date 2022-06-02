@file:JvmSynthetic

package com.payu.baas.coreUI.model.datasource

import com.payu.baas.coreUI.model.entities.model.TypeModel

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
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.ALL_TIME,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.ALL_TIME,
                    false
                ), TypeModel(
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.LAST_MONTH,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.LAST_MONTH,
                    false
                ), TypeModel(
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.LAST_3_MONTHS,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.LAST_3_MONTHS,
                    false
                ), TypeModel(
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.LAST_1_YEAR,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.LAST_1_YEAR,
                    false
                ), TypeModel(
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.CUSTOM_TIME_PERIOD,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.CUSTOM_TIME_PERIOD,
                    false
                )
            )
        }

    val transactionTypes: ArrayList<TypeModel>
        get() {
            return arrayListOf(
                TypeModel(
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.ALL,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.ALL,
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
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.ALL,
                    com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.ALL,
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