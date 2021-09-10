package com.thinxnet.rydpay.websdkintegration

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.*
import androidx.core.app.BundleCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import com.thinxnet.rydpay.R
import com.thinxnet.rydpay.websdkintegration.model.PaymentDataDto
import com.thinxnet.rydpay.websdkintegration.model.PaymentDataDtoMapper
import com.thinxnet.rydpay.websdkintegration.model.RydPayWebSdkCallbackResult
import kotlinx.serialization.json.Json

class CCTHandlerActivity : AppCompatActivity() {

    private val json: Json by lazy {
        Json {
            isLenient = true
        }
    }

    private val autoFinishWithCancelRunnable = Runnable {
        if (!isStartedWithCallbackIntent) {
            finishWithCancel()
        }
    }

    private val autoFinisHandler = Handler(Looper.getMainLooper())

    private val isStartedWithInitialIntent: Boolean
        get() = intent?.getParcelableExtra<Uri>(INTENT_EXTRA_URI) != null

    private val isStartedWithCallbackIntent: Boolean
        get() = intent?.data != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            isStartedWithCallbackIntent -> {
                // Most probably activity was killed in the background, and is now created due to
                // the custom url schema callback
                val callBackUri = checkNotNull(intent?.data) {
                    "Unexpected null callback intent data"
                }
                processCallbackUri(callBackUri)
            }
            isStartedWithInitialIntent -> {
                // Open the CCT only if this onCreate is the initial one and is not
                // cause by a config change or the Activity being killed in the background
                if (savedInstanceState == null) {
                    val initialUri =
                        checkNotNull(intent.getParcelableExtra<Uri>(INTENT_EXTRA_URI)) {
                            "Unexpected null initial intent extra URI"
                        }
                    // Manually build and launch empty CCT intent,
                    // since there is no support in the CCT public API for
                    // startActivityForResult calls.
                    val cctIntent = Intent(Intent.ACTION_VIEW).apply {
                        putExtra(EXTRA_ENABLE_INSTANT_APPS, true)
                        putExtra(EXTRA_SHARE_STATE, SHARE_STATE_DEFAULT)
                        putExtra(
                            EXTRA_TOOLBAR_COLOR,
                            ContextCompat.getColor(this@CCTHandlerActivity, R.color.light_navy_blue)
                        )
                        val requiredSession = Bundle().apply {
                            putBinder(EXTRA_SESSION, null)
                        }
                        putExtras(requiredSession)
                        data = initialUri
                    }
                    startActivityForResult(cctIntent, CCT_REQUEST_CODE)
                }
            }
            else -> finishWithCancel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CCT_REQUEST_CODE) {
            // We just came back from the CCT. We should wait a bit to be sure that
            // no onNewIntent call will happen before we can assume the user simply closed the CCT
            autoFinisHandler.postDelayed(autoFinishWithCancelRunnable, 500)
        } else {
            finishWithCancel()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // We have received a new intent. If we have any pending auto-close,
        // we should cancel it, since we want to provide back the successful result if possible
        autoFinisHandler.removeCallbacks(autoFinishWithCancelRunnable)
        val callbackUri = intent?.data
        if (callbackUri == null) {
            finishWithCancel()
            return
        }
        processCallbackUri(callbackUri)
    }

    private fun processCallbackUri(callbackUri: Uri) {
        val isPaymentSuccessful = callbackUri.host == "finish"
        val paymentDataString = callbackUri.getQueryParameter("paymentdata")
        if (isPaymentSuccessful && paymentDataString != null) {
            try {
                val paymentData =
                    json.decodeFromString(PaymentDataDto.serializer(), paymentDataString)
                finishWithResult(
                    RydPayWebSdkCallbackResult.Success(PaymentDataDtoMapper.transform(paymentData))
                )
            } catch (ex: Exception) {
                finishWithResult(
                    RydPayWebSdkCallbackResult.Failure("Failed to parse result from:\n${paymentDataString}")
                )
            }

        } else {
            finishWithResult(
                RydPayWebSdkCallbackResult.Failure("Unexpected callbackUri path or params:\n${callbackUri}")
            )
        }
    }

    private fun finishWithCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun finishWithResult(rydPayWebSdkCallbackResult: RydPayWebSdkCallbackResult) {
        setResult(
            RESULT_OK,
            Intent().putExtra(INTENT_EXTRA_CALLBACK_RESULT, rydPayWebSdkCallbackResult)
        )
        finish()
    }

    companion object {

        private const val INTENT_EXTRA_URI = "INTENT_EXTRA_URI"

        private const val INTENT_EXTRA_CALLBACK_RESULT = "INTENT_EXTRA_CALLBACK_RESULT"
        private const val CCT_REQUEST_CODE = 42

        fun createLaunchIntent(context: Context, uri: Uri): Intent =
            Intent(context, CCTHandlerActivity::class.java).apply {
                putExtra(INTENT_EXTRA_URI, uri)
            }

        fun parseCallbackResultIntent(intent: Intent): RydPayWebSdkCallbackResult? =
            intent.getParcelableExtra(INTENT_EXTRA_CALLBACK_RESULT)
    }
}