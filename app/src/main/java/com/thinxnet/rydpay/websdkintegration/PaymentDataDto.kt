package com.thinxnet.rydpay.websdkintegration

import java.math.BigDecimal

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class PaymentDataDto(
    val amount: Double,
    val price: BigDecimal,
    val total: BigDecimal,
    val stationId: String
)