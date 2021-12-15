package com.hugegreenbug.launchy

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import java.net.URL
import kotlin.concurrent.thread
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.util.concurrent.locks.ReentrantLock

@SuppressLint("CustomSplashScreen")
class UnsplashActivity : AppCompatActivity() {
    private lateinit var wallList: RecyclerView
    private lateinit var adapter: UnsplashAdapter
    private lateinit var titleText: TextView
    private lateinit var category: String
    private lateinit var progress:ProgressBar
    private lateinit var prev:Button
    private lateinit var next:Button
    private lateinit var loading: LinearLayout

    @Volatile
    private var dialog:AlertDialog? = null

    private val wallData: ArrayList<Unsplash> = ArrayList()
    private val wallDataLock = ReentrantLock()
    private val startState = "start"
    private val nextState = "next"
    private val prevState = "prev"
    private val clientId = "client_id="
    @Volatile
    private var pageNumber: Int = 1
    @Volatile
    private var imagesShown: Int = 0
    @Volatile
    private var totalImages: Int = 0
    @Volatile
    private var imagesOnPage: Int = 0

    private val colCount = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash)
        val extras = intent.extras
        if (extras != null) {
            category = extras.getString(unsplashKey).toString()
        }

        titleText = findViewById(R.id.title)
        prev = findViewById(R.id.previousPage)
        next = findViewById(R.id.nextPage)
        loading = findViewById(R.id.loading)
        initWallList()
        initButtons()
        getWalls(startState)
    }

    private fun initButtons() {
        prev.setOnClickListener {
            getWalls(prevState)
        }
        next.setOnClickListener{
            getWalls(nextState)
        }
    }
    private fun initWallList() {
        adapter = UnsplashAdapter(wallData)
        adapter.setHasStableIds(true)
        adapter.itemLongClickListener = { unsplash -> setWallOnClick(unsplash) }
        adapter.itemClickListener = { unsplash -> launchWebviewOnClick(unsplash) }
        wallList = findViewById(R.id.wallList)
        wallList.layoutManager = PredictiveGridLayoutManager(this, colCount)
        wallList.adapter = adapter
        progress = findViewById(R.id.progressBar_cyclic)
        wallList.visibility = View.GONE
    }

    private fun showAlert(message:String, state:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(message)
        builder.setPositiveButton(R.string.retry) { _, _ ->
            getWalls(state)
            dialog = null
        }

        if (state != startState) {
            builder.setNeutralButton(R.string.last) { _, _ ->
                when (state) {
                    startState -> {
                        getWalls(state)
                    }
                    prevState -> {
                        pageNumber += 1
                        getWalls(nextState)
                    }
                    nextState -> {
                        pageNumber -= 1
                        getWalls(prevState)
                    }
                }

                dialog = null
            }
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            dialog = null
            finish()
        }

        dialog = builder.show()
    }

    private fun startUnsplashWebActivity(name:String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val url = "https://unsplash.com/$name?utm_source=launchy&utm_medium=referral"
            val intent = Intent(this, UnsplashWebview::class.java)
            intent.putExtra(UnsplashWebview.unsplashWebKey, url)
            startActivity(intent)
        }, 100)
    }

    private fun launchWebviewOnClick(unsplash:Unsplash) {
        startUnsplashWebActivity(unsplash.name)
    }

    private fun setWallOnClick(unsplash:Unsplash): Boolean {
        thread {
            runOnUiThread {
                Toast.makeText(applicationContext, R.string.downloading_wallpaper, Toast.LENGTH_SHORT).show()
            }

            val response = URL(unsplash.url).readText()
            val parsedJson = parseUrl(response)
            if (parsedJson == null) {
                runOnUiThread {
                    Toast.makeText(applicationContext, R.string.wallpaper_error, Toast.LENGTH_SHORT).show()
                }
                finish()
                return@thread
            }
            val url: String? = parsedJson.string("url")
            val bmap = getBitmapFromURL(url)
            if (bmap == null) {
                runOnUiThread {
                    Toast.makeText(applicationContext, R.string.wallpaper_error, Toast.LENGTH_SHORT).show()
                }
            } else {
                BitmapUtils.setWallpaper(bmap.toDrawable(resources), this)
                runOnUiThread {
                    Toast.makeText(applicationContext, R.string.set_wallpaper, Toast.LENGTH_SHORT).show()
                }
            }
        }

        finish()
        return true
    }

    private fun hideShowNext() {
        if (imagesShown >= totalImages) {
            next.visibility = View.INVISIBLE
        } else {
            next.visibility = View.VISIBLE
        }
    }

    private fun hideShowPrev() {
        if (pageNumber > 1) {
            prev.visibility = View.VISIBLE
        } else {
            prev.visibility = View.INVISIBLE
        }
    }

    private fun getWallsError(state:String) {
        wallDataLock.unlock()
        runOnUiThread {
            progress.visibility = View.INVISIBLE
            hideShowPrev()
            hideShowNext()
            showAlert(getString(R.string.nowalls), state)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getWalls(state:String) {
        var url:String
        val title:String
        val catId:String
        var newState = state
        when (category) {
            nature -> {
                title = "Unsplash Nature Collection"
                catId = "3330448"
            }
            animals -> {
                title = "Unsplash Animals Collection"
                catId = "3330452"
            }
            textures -> {
                title = "Unsplash Textures Collection"
                catId = "3330445"
            }
            drone -> {
                title = "Unsplash Drone Captures Collection"
                catId = "291437"
            }
            wallpapers -> {
                title = "Unsplash Wallpapers Collection"
                catId = "1065976"
            }
            editorial -> {
                title = "Unsplash Editorial Collection"
                catId = "317099"
            }
            else -> {
                title = "Unsplash Nature Collection"
                catId = "3330448"
            }
        }

        var attemptPageNumber = pageNumber
        if (state == nextState) {
            attemptPageNumber = pageNumber + 1
        } else if (state == prevState) {
            attemptPageNumber = pageNumber - 1
        }
        url = "https://api.unsplash.com/collections/" + catId +
                "/photos?" + clientId + "&page=1&orientation=landscape&per_page=16"
        url += "&page=$attemptPageNumber"
        runOnUiThread {
            progress.visibility = View.VISIBLE
            titleText.text = title
        }

        thread {
            wallDataLock.lock()
            val newWallData: ArrayList<Unsplash> = ArrayList()
            try {
                val response: String?
                val conn = URL(url).openConnection()
                val header = conn.getHeaderField("X-Total")

                totalImages = try {
                    header.toInt()
                } catch (ne: NumberFormatException) {
                    0
                }

                response = URL(url).readText()
                val parsedJson = parse(response)
                if (parsedJson == null) {
                    getWallsError(state)
                    return@thread
                }
                if (state == prevState) {
                    pageNumber -= 1
                    imagesShown -= imagesOnPage

                    if (pageNumber <= 1) {
                        newState = startState
                        pageNumber = 1
                        imagesShown = 0
                    }
                } else if (state == nextState) {
                    pageNumber += 1
                }

                imagesOnPage = 0
                for (obj in parsedJson) {
                    imagesOnPage += 1
                    val user = obj.obj("user")
                    val urls = obj.obj("urls")
                    val links = obj.obj("links")
                    val fullUrl = links?.string("download_location") + "&" + clientId
                    val thumb = getBitmapFromURL(urls?.string("small"))
                    var author = user?.string("name")
                    author = author?.toCharArray()
                        ?.filter { it.isLetterOrDigit() || it.isWhitespace() }
                        ?.joinToString(separator = "")
                    var name: String? = user?.string("username")
                    if (name == null) {
                        name = "Unknown"
                    }
                    if (author != null && fullUrl != null && thumb != null) {
                        newWallData.add(Unsplash(author, fullUrl, thumb, name))
                    }
                }

                if (newState == nextState || newState == startState) {
                    imagesShown += imagesOnPage
                }

            } catch (e: Exception) {
                getWallsError(state)
                return@thread
            }
            if (newWallData.size == 0) {
                getWallsError(state)
                return@thread
            }
            runOnUiThread {
                wallData.clear()
                adapter.notifyDataSetChanged()
                wallData.addAll(newWallData)
                adapter.notifyDataSetChanged()
                progress.visibility = View.GONE
                wallList.visibility = View.VISIBLE
                loading.visibility = View.GONE
                wallList.scrollToPosition(0)
                wallList.requestFocus()
                hideShowNext()
                hideShowPrev()
                if (state == startState) {
                    Toast.makeText(applicationContext, R.string.select_wallpaper, Toast.LENGTH_LONG).show()
                }
            }
            wallDataLock.unlock()
        }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parse(str: String?): JsonArray<JsonObject>? {
        if (str == null) {
            return null
        }
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(str)

        return parser.parse(stringBuilder) as? JsonArray<JsonObject>
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseUrl(str: String?): JsonObject? {
        if (str == null) {
            return null
        }
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(str)

        return parser.parse(stringBuilder) as? JsonObject
    }

    companion object {
        const val unsplashKey:String="unsplash"
        const val nature:String="nature"
        const val animals:String="animals"
        const val textures:String="textures"
        const val drone:String="drone"
        const val wallpapers:String="wallpapers"
        const val editorial:String="editorial"
    }
}
