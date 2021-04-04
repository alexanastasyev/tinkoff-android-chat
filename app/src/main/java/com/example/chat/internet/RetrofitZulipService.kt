package com.example.chat.internet

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RetrofitZulipService {

    private const val email = "alex.anastasyev@mail.ru"
    private const val apiKey = "QGBIOe7ritlaJqqwar03NWMVZtF9aTRd"
    private const val baseUrl = "https://tfs-android-2021-spring.zulipchat.com/api/v1/"

    private val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .authenticator(object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request {
                val credential: String = Credentials.basic(email, apiKey)
                return response.request.newBuilder().header("Authorization", credential).build()
            }
        }).apply {
            this.addInterceptor(interceptor)
        }.build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(Json {
            ignoreUnknownKeys = true
        }.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    private val zulipService = retrofit.create(ZulipServiceInterface::class.java)

    fun getInstance() : ZulipServiceInterface {
        return zulipService
    }
}