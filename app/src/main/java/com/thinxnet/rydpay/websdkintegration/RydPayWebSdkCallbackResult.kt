package com.thinxnet.rydpay.websdkintegration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

sealed class RydPayWebSdkCallbackResult : Parcelable {

    @Parcelize
    data class Success(
        paymentData: PaymentDataDto
    ): RydPayWebSdkCallbackResult()

    @Parcelize
    data class Failure(
        val payload: String
    ): RydPayWebSdkCallbackResult()
}
