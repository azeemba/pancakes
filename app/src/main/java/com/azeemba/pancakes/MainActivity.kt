package com.azeemba.pancakes

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.room.Room.databaseBuilder
import com.azeemba.pancakes.databinding.ActivityMainBinding
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webview: WebView

    private lateinit var db: Storage

    private fun makeDb(): Storage {
        return databaseBuilder(
            applicationContext,
            Storage::class.java, "azeemba.pancakes.storage"
        ).build()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        val log = Logger.getLogger("Pancakes")
        super.onCreate(savedInstanceState)

        db = makeDb()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stackUrl = "https://stackexchange.com/"
        val script = """
            x = document.getElementsByTagName('head')[0]
            y = document.createElement('meta')
            y.name="viewport"
            y.content="width=device-width, initial-scale=1.0"
            x.appendChild(y)
            function unsetWidth(thing) { thing.style.width = 'auto'}
            function unsetAll(things) { for (let i = 0; i < things.length; ++i) unsetWidth(things[i])}
            function doAll(match) {unsetAll(document.getElementsByClassName(match))}
            document.getElementById("mainArea").style.width="auto"
            doAll("contentWrapper")
            doAll("question-container")
            doAll("question question-hot")
            doAll("question")
        """.trimIndent()

        webview = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webview.settings.forceDark = WebSettings.FORCE_DARK_ON
        }
        webview.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val title = webview.title
                if (url != null && title != null) {
                    val visit = makeNowVisit(url, title)

                    val stackDomain = "stackexchange"
                    if (visit.community == stackDomain) webview.evaluateJavascript(script) {}
                    else {
                        Thread(Runnable {
                            db.visitDao().insert(visit)
                            log.info(visit.toString())
                        }).start()
                    }
                }
                else {
                    log.info("Something was null: url - $url title - $title")
                }
                super.onPageFinished(view, url)
            }
        }

        webview.setOnLongClickListener {
            log.info("In long click")
            val intent = Intent(webview.context, VisitListView::class.java)
            startActivity(intent)
            false
        }

        webview.loadUrl(stackUrl)

        binding.fab.setOnClickListener { view ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Pancake page", webview.originalUrl)
            clipboard.setPrimaryClip(clip)

            Snackbar.make(view, "${webview.originalUrl} copied", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        when {
            webview.canGoBack() -> webview.goBack()
            else -> super.onBackPressed()
        }
    }
}