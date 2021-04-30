package com.thinxnet.rydpay.websdkintegration.model

import java.math.BigDecimal

object PaymentDataDtoMapper {
    fun transform(paymentDataDto: PaymentDataDto): PaymentData {
        return PaymentData(
            amount = paymentDataDto.amount,
            price = BigDecimal(paymentDataDto.price),
            total = BigDecimal(paymentDataDto.total),
            stationId = paymentDataDto.stationId,
        )
    }
}