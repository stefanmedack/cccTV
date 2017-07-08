package info.metadude.kotlin.library.c3media

import com.squareup.moshi.Moshi
import info.metadude.kotlin.library.c3media.adapters.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiModule {

    fun provideC3MediaService(baseUrl: String,
                              okHttpClient: OkHttpClient
    ): C3MediaService = createRetrofit(baseUrl, okHttpClient)
            .create(C3MediaService::class.java)


    private fun createRetrofit(baseUrl: String,
                               okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
                .add(OffsetDateTimeAdapter())
                .add(LocalDateAdapter())
                .add(LanguageAdapter())
                .add(AspectRatioAdapter())
                .add(MimeTypeAdapter())
                .build()

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
    }

}
