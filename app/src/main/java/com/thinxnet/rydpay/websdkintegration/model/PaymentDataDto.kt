package com.thinxnet.rydpay.websdkintegration.model

import java.math.BigDecimal

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class PaymentDataDto(
    val amount: Double,
    val price: String,
    val total: String,
    val stationId: String
)