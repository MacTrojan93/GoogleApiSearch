package com.example.googlebooksapi

import android.net.Uri
import android.os.Handler
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

//https://www.googleapis.com/books/v1/volumes?q=pride+prejudice&maxResults=5&printType=books

const val BASE_URL = "https://www.googleapis.com/"
const val ENDPOINT = "books/v1/volumes?"
const val Q_PARAM = "q"
const val MAX_RESULTS_PARAM = "maxResults"
const val PRINT_TYPE_PARAM = "printType"

class Network(val listener: UpdateData, val handler: Handler) {

    private lateinit var bookTitle: String
    private lateinit var bookMaxResult: String
    private lateinit var bookType: String

    fun setBookTitle(title: String){
        bookTitle = title
    }

    fun setBookMaxResult(maxResult: String){
        bookMaxResult = maxResult
    }

    fun setBookType(bookType: String){
        this.bookType = bookType
    }

    private val URI by lazy {
        Uri.parse("$BASE_URL$ENDPOINT").buildUpon()
            .appendQueryParameter(Q_PARAM, "$bookTitle")
            .appendQueryParameter(MAX_RESULTS_PARAM, "$bookMaxResult")
            .appendQueryParameter(PRINT_TYPE_PARAM, "$bookType")
            .build()
    }

   // private val url: URL = URL(URI.toString())

    fun configureNetworkConnection() {
        val url: URL = URL(URI.toString())
        val httpUrlConnection = url.openConnection() as HttpURLConnection
        httpUrlConnection.readTimeout = 10000
        httpUrlConnection.connectTimeout = 10000
        httpUrlConnection.requestMethod = "GET"
        httpUrlConnection.doInput = true
        httpUrlConnection.connect()

        val inputStream = httpUrlConnection.inputStream
        val responseCode = httpUrlConnection.responseCode

        val jsonResponse = convertIsToString2(inputStream)
        jsonResponse?.let {
            val booksResponse = deserializeJsonResponse(jsonResponse)
            handler.post {
                listener.sendData(booksResponse)
            }
        }

    }

    private fun deserializeJsonResponse(input: String): BooksResponse {

        val booksResponse: BooksResponse
        var booksItemDescription: ItemsDescription
        val booksListItemsDescription = mutableListOf<ItemsDescription>()

        val jsonResponse: JSONObject = JSONObject(input)
        val jsonArrayItems = jsonResponse.getJSONArray("items")
        for (index in 0 until jsonArrayItems.length()) {
            val jsonElement: JSONObject = jsonArrayItems[index] as JSONObject
            val jsonVolumeInfo = jsonElement.getJSONObject("volumeInfo")
            var booksItemInfo : VolumeInfo
            try {
                 booksItemInfo = VolumeInfo(
                    jsonVolumeInfo.getString("title"),
                    jsonVolumeInfo.getString("subtitle")
                )
            }catch (exception: Exception) {
                 booksItemInfo = VolumeInfo(
                    "N/A",
                    "N/A"
                )
            }
            booksItemDescription = ItemsDescription(booksItemInfo)
            booksListItemsDescription.add(booksItemDescription)

        }
        booksResponse = BooksResponse(booksListItemsDescription)
        return booksResponse
    }

    fun convertIsToString(stream: InputStream?, len: Int): String? {
        var reader: Reader? = null
        reader = InputStreamReader(stream, "UTF-8")
        val buffer = CharArray(len)
        reader.read(buffer)
        return String(buffer)
    }

    fun convertIsToString2(inputStream: InputStream): String? {
        val builder = StringBuilder()
        val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))

        var line: String? = reader.readLine()

        while (line != null) {
            builder.append(line)
            line = reader.readLine()
        }
        if (builder.isEmpty()) {
            return null
        }
        return builder.toString()
    }

}


