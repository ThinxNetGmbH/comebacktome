package dev.luboganev.comebacktome

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent

class CCTHandlerActivity : AppCompatActivity() {

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = intent.getParcelableExtra(INTENT_EXTRA_URI)

        if (savedInstanceState == null) {
            val currentUri = uri
            if (currentUri != null) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, currentUri)
            } else {
                finishWithCancel()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            finishWithCancel()
            return
        }

        val callbackUri = intent.data
        if (callbackUri == null) {
            finishWithCancel()
            return
        }

        val simpleStringData = callbackUri.getQueryParameter("simpleStringData")
        val simpleNumberData = callbackUri.getQueryParameter("simpleNumberData")?.toLongOrNull()
        val complexData = callbackUri.getQueryParameter("complexData")

        if (simpleStringData != null &&
            simpleNumberData != null &&
            complexData != null
        ) {
            finishWithSuccess(
                CallbackResult(
                    simpleStringData = simpleStringData,
                    simpleNumberData = simpleNumberData,
                    complexDataInJson = complexData
                )
            )
        } else {
            finishWithCancel()
        }
    }

    private fun finishWithCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun finishWithSuccess(callbackResult: CallbackResult) {
        setResult(RESULT_OK, Intent().putExtra(INTENT_EXTRA_CALLBACK_RESULT, callbackResult))
        finish()
    }

    companion object {

        private const val INTENT_EXTRA_URI = "INTENT_EXTRA_URI"

        private const val INTENT_EXTRA_CALLBACK_RESULT = "INTENT_EXTRA_CALLBACK_RESULT"

        fun createLaunchIntent(context: Context, uri: Uri): Intent =
            Intent(context, CCTHandlerActivity::class.java).apply {
                putExtra(INTENT_EXTRA_URI, uri)
            }

        fun parseCallbackResultIntent(intent: Intent): CallbackResult? =
            intent.getParcelableExtra(INTENT_EXTRA_CALLBACK_RESULT)
    }
}