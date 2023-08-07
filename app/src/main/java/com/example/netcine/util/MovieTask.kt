package com.example.netcine.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netcine.model.Category
import com.example.netcine.model.Movie
import com.example.netcine.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback:Callback) {
    private val handler = Handler(Looper.getMainLooper())
   private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
    fun onPreExecute()
    fun onResult(movieDetail: MovieDetail)
    fun onFailure(message:String)
    }

    fun execute(url: String) {
        callback.onPreExecute()

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null

            try {


                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode: Int = urlConnection.responseCode

                if(statusCode == 400){
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonAString = toString(buffer)

                  val json = JSONObject(jsonAString)
                  val message = json.getString("messege")
                    throw IOException(message)

                }else if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o sevidor!")
                }
                stream = urlConnection.inputStream


                ///val jsonString = stream.bufferedReader().use { it.readText() }
                buffer = BufferedInputStream(stream)
                val jsonAString = toString(buffer)

                val movieDetail = toMovieDetail(jsonAString)
                handler.post {
                    callback.onResult(movieDetail)
                }

            } catch (e: IOException) {
                val message = e.message ?: "erro desconhecido"
                Log.e("Teste", message, e)

                handler.post {

                    callback.onFailure(message)
                }

                } finally {
                    urlConnection?.disconnect()
                    stream?.close()
                    buffer?.close()

                }
            }
        }


    private fun toCategories(jsonAString: String): List<Category> {
        val categories = mutableListOf<Category>()


        val jsonRoot = JSONObject(jsonAString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }
            categories.add(Category(title, movies))
        }
        return categories
    }
    private fun toMovieDetail(jsonAString: String):MovieDetail {
        val json = JSONObject(jsonAString)
        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val simlars = mutableListOf<Movie>()
        for (i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarCoverUrl)
            simlars.add(m)
        }
        val movie = Movie(id, coverUrl,title,desc,cast)
        return MovieDetail(movie,simlars)
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)

            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }
}
