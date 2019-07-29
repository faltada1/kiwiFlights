package cz.danfalta.kiwi.service.repository.network

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import cz.danfalta.kiwi.KiwiApplication
import cz.danfalta.kiwi.util.SingletonHolder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class RetrofitManager private constructor(private val application: KiwiApplication) {

    companion object : SingletonHolder<RetrofitManager, KiwiApplication>(::RetrofitManager)

    //Common settings
    private var cacheSize: Long = 10 * 1024 * 1024 // 10 MB
    private var cache = Cache(application.cacheDir, cacheSize)

    //Lazy initialization of flightServiceAPI
    open val flightService: FlightService by lazy {

        val serviceClass = FlightService::class.java

        val okHttpClient = OkHttpClient.Builder()
            //.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(offlineInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .cache(cache)
            .build()

        return@lazy Retrofit.Builder()
            .baseUrl("https://api.skypicker.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    private var offlineInterceptor: Interceptor = Interceptor { chain ->
        var request = chain.request()
        if (!isNetworkAvailable()) {
            val maxStale = 60 * 60 * 24 * 30 // Offline cache available for 30 days
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                //Override server caching
                .removeHeader("Pragma")
                .build()
        }
        chain.proceed(request)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = application.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
