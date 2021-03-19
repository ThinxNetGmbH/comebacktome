package dev.luboganev.comebacktome

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallbackResult(
    val simpleStringData: String,
    val simpleNumberData: Long,
    val complexDataInJson: String
) : Parcelable
