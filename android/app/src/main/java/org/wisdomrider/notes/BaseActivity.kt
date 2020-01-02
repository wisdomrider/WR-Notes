package org.wisdomrider.notes

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.wisdomrider.Utils.Preferences
import com.wisdomrider.sqliteclosedhelper.SqliteClosedHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class BaseActivity : com.wisdomrider.Activities.BaseActivity() {
    val BASE_URL = "https://notes.wisdomriderr.shop/api/mobile/"
    lateinit var retrofit: Retrofit
    lateinit var preferences: Preferences
    val gson = Gson()
    lateinit var helper: SqliteClosedHelper
    lateinit var validator: Validator
    lateinit var api: Api
    var progess: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logging = HttpLoggingInterceptor()
        preferences = Preferences(this, "Data", 0)
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
        validator = Validator()
        helper = SqliteClosedHelper(this, "NOTES")
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Authorization", "Bearer " + preferences.getString("token", "notes"))
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
        httpClient.addInterceptor(logging)
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(Api::class.java)

    }


    fun showAlert(message: String) {
        val alert = AlertDialog.Builder(this)
        alert.setMessage(message)
        alert.show()
    }

    fun closeProgressBar() {
        if (progess != null)
            progess!!.cancel()
    }

    private fun showProgessBar(cont: String) {
        if (progess == null)
            progess = ProgressDialog(this)
        progess!!.setCancelable(false)
        progess!!.setMessage("Loading...")
        progess!!.setTitle(cont)
        progess!!.show()
    }

    public interface Do {
        fun <T> Do(body: T?)
    }

    fun <T> Call<T>.get(
        what: String,
        after: Do? = null,
        messageFor406: String = "Authentication Error",
        messageFor400: String = "Unable to connect to server please try again !",
        onBack: Boolean = false,
        on406: Do? = null,
        onNetworkError: Do? = null
    ): Any {
        if (!what.isEmpty())
            showProgessBar(what)
        this.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                closeProgressBar()
                if (onNetworkError != null) {
                    onNetworkError.Do(null)
                    return
                }
                showAlert("Unable to connect to the server.")
                if (onBack) {
                    wisdom.toast("Please check your internet connection.")
                    onBackPressed()
                }

            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                closeProgressBar()
                Log.e("CODE", "" + response.message())
                when (response.code()) {
                    200 -> if (after != null) after.Do(response.body())
                    406 -> {
                        if (on406 == null) showAlert(messageFor406)
                        else on406.Do(null)
                    }
                    400 -> showAlert(messageFor400)
                    401 -> {
                        if (!what.isEmpty())
                            showAlert(messageFor406)
//                        else
//                            startActivity(Intent(applicationContext, TrackerPage::class.java))
                    }
                    500 -> showAlert(messageFor400)
                    123 -> showAlert(response.errorBody()!!.string())
                    else -> showAlert("Please check your internet connection.")
                }

            }
        })


        return this
    }


}
