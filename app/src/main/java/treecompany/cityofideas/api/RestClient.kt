package treecompany.cityofideas.api

import okhttp3.OkHttpClient
import java.security.cert.*
import javax.net.ssl.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.Gson

object RestClient{
    const val BASE_URL_LOCAL="https://10.0.2.2:5001/"
    const val BASE_URL_GCLOUD="https://35.187.43.105/"

    private var retrofit: Retrofit? = null

    var gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    fun getClient(): Retrofit {
        if (retrofit == null) {
            val client = getUnsafeOkHttpClient()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_GCLOUD)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }
            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
