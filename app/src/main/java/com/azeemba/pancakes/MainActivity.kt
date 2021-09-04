package com.azeemba.pancakes

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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
                val log = Logger.getLogger("Pancakes")
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

        webview.loadUrl(stackUrl)

//        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}