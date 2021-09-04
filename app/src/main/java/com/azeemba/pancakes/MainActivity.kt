package com.azeemba.pancakes

import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.azeemba.pancakes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = "https://stackexchange.com/"
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

        val x = findViewById<WebView>(R.id.webview)
        x.settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            x.settings.forceDark = WebSettings.FORCE_DARK_ON
        }
        x.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, _url: String?) {
                super.onPageFinished(view, _url)
                if (_url == url) {
                    x.evaluateJavascript(script) {}
                }
            }
        }

        x.canGoBack()
        x.loadUrl(url)

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

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}