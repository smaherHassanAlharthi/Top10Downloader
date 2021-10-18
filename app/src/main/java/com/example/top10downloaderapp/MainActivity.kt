package com.example.top10downloaderapp

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var myRv: RecyclerView
    lateinit var rvAdapter: RVAdapter
    lateinit var feeds : ArrayList<Feeds>
    val parser = XMLParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        myRv = findViewById(R.id.rvFeeds)


        val fab = findViewById<FloatingActionButton>(R.id.fabFetch)
        fab.setOnClickListener {
            getFeeds()
        }

    }

    private fun getFeeds() {
        CoroutineScope(Dispatchers.IO).launch {

             feeds = async {
                fetch()
            }.await()

            withContext(Dispatchers.Main) {
                setRV()
            }

        }
    }

    fun setRV() {
        rvAdapter = RVAdapter(feeds, this@MainActivity)
        myRv.adapter = rvAdapter
        myRv.layoutManager = LinearLayoutManager(applicationContext)
    }

    fun fetch(): ArrayList<Feeds> {
        val url =
            URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        val urlConnection = url.openConnection() as HttpURLConnection
        feeds =

            urlConnection.getInputStream()?.let {
                parser.parse(it)
            }
                    as ArrayList<Feeds>
        return feeds
    }

}