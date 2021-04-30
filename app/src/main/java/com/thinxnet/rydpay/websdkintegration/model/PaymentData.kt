package com.thinxnet.rydpay.websdkintegration.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentData(
    val amount: Double,
    val price: BigDecimal,
    val total: BigDecimal,
    val stationId: String
) : Parcelable