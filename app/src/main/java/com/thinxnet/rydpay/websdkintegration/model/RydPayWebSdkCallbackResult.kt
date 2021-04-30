package com.thinxnet.rydpay.websdkintegration.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class RydPayWebSdkCallbackResult : Parcelable {

    @Parcelize
    data class Success(
        val paymentData: PaymentData
    ): RydPayWebSdkCallbackResult()

    @Parcelize
    data class Failure(
        val payload: String
    ): RydPayWebSdkCallbackResult()
}
