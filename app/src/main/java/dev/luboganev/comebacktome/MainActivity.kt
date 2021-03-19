package dev.luboganev.comebacktome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.luboganev.comebacktome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.goButton.setOnClickListener {
            val inputUrlString = viewBinding.urlEditText.text?.toString() ?: ""
            try {
                val uri = Uri.parse(inputUrlString)
                startActivityForResult(
                    CCTHandlerActivity.createLaunchIntent(this, uri),
                    CHROME_CUSTOM_TAB_REQUEST_CODE
                )
            } catch (ex: Exception) {
                viewBinding.comeBackResultsText.text = "Invalid url: $inputUrlString"
            }
        }

        viewBinding.customUrlSchemeText.text = BuildConfig.CUSTOM_URL_SCHEMA
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHROME_CUSTOM_TAB_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    viewBinding.comeBackResultsText.text =
                        "Callback result:\n${CCTHandlerActivity.parseCallbackResultIntent(data)}"
                } else {
                    viewBinding.comeBackResultsText.text =
                        "Came back from chrome custom tab with OK result but no data"
                }
            } else {
                viewBinding.comeBackResultsText.text =
                    "Came back from chrome custom tab with non-OK RESULT: $resultCode"
            }
        }
    }

    companion object {
        private const val CHROME_CUSTOM_TAB_REQUEST_CODE = 1337
    }
}