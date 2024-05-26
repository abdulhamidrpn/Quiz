package com.education.ekagratagkquiz.main.presentation.composables

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun PDFWebView(pdfUrl: String) {

    /*AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                settings.pluginState = WebSettings.PluginState.ON
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
        },
        update = { webView ->
//            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
            webView.loadUrl(pdfUrl)

        }
    )*/
}
