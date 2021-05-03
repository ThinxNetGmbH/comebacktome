package com.thinxnet.rydpay.websdkintegration

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thinxnet.rydpay.BuildConfig
import com.thinxnet.rydpay.databinding.ActivityMainBinding

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.goButton.setOnClickListener {
            val inputUrlString = viewBinding.urlEditText.text?.toString()?.trim() ?: ""
            try {
                val uri = Uri.parse(inputUrlString)
                startActivityForResult(
                    CCTHandlerActivity.createLaunchIntent(this, uri),
                    CHROME_CUSTOM_TAB_REQUEST_CODE
                )
            } catch (ex: Exception) {
                viewBinding.rydPayWebSdkResultsText.text = "Invalid url: $inputUrlString"
            }
        }

        viewBinding.clearUrlButton.setOnClickListener {
            viewBinding.urlEditText.text = null
        }

        viewBinding.defaultHomeUrlButton.setOnClickListener {
            viewBinding.urlEditText.setText("https://ryd-demo.web.app/?callback=true")
        }

        viewBinding.defaultStationUrlButton.setOnClickListener {
            viewBinding.urlEditText.setText("https://ryd-demo.web.app/?pid=5f746bf05bce72222d327778&callback=true")
        }

        viewBinding.customUrlSchemeText.text = BuildConfig.CUSTOM_URL_SCHEMA
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHROME_CUSTOM_TAB_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    viewBinding.rydPayWebSdkResultsText.text =
                        "Callback result:\n${CCTHandlerActivity.parseCallbackResultIntent(data)}"
                } else {
                    viewBinding.rydPayWebSdkResultsText.text =
                        "Callback from chrome custom tab with OK result but no data"
                }
            } else {
                viewBinding.rydPayWebSdkResultsText.text =
                    "Came back from chrome custom tab with non-OK RESULT: $resultCode"
            }
        }
    }

    companion object {
        private const val CHROME_CUSTOM_TAB_REQUEST_CODE = 1337
    }
}