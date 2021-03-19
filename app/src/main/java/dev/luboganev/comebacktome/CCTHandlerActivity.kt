package dev.luboganev.comebacktome

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CCTHandlerActivity : AppCompatActivity() {

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = intent.getParcelableExtra(INTENT_EXTRA_URI)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        if (intent.extras?.containsKey(INTENT_EXTRA_URI) == true) {
            uri = intent.getParcelableExtra(INTENT_EXTRA_URI)
        }
    }

    companion object {

        private const val INTENT_EXTRA_URI = "INTENT_EXTRA_URI"

        fun createLaunchIntent(context: Context, uri: Uri): Intent =
            Intent(context, CCTHandlerActivity::class.java).apply {
                putExtra(INTENT_EXTRA_URI, uri)
            }
    }
}