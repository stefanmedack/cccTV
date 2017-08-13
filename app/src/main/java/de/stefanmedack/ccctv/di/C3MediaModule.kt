package de.stefanmedack.ccctv.di

import android.content.Context
import dagger.Module
import dagger.Provides
import de.stefanmedack.ccctv.di.C3TVScopes.ApplicationContext
import de.stefanmedack.ccctv.di.C3TVScopes.CacheDir
import de.stefanmedack.ccctv.util.CACHE_MAX_SIZE_HTTP
import info.metadude.kotlin.library.c3media.ApiModule.provideRxC3MediaService
import info.metadude.kotlin.library.c3media.RxC3MediaService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import javax.inject.Singleton

@Module
class C3MediaModule {

    @Provides
    @Singleton
    fun provideC3MediaService(@CacheDir cacheDir: File?): RxC3MediaService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        val okHttpClient = OkHttpClient.Builder().run {
            addNetworkInterceptor(interceptor)
            if (null != cacheDir) {
                val responseCache = File(cacheDir, "HttpResponseCache")
                if (!responseCache.exists()) {
                    responseCache.mkdirs()
                }
                cache(Cache(responseCache, CACHE_MAX_SIZE_HTTP))
            }
            build()
        }
        return provideRxC3MediaService("https://api.media.ccc.de", okHttpClient)
    }

    @Provides
    @CacheDir
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): File = context.cacheDir
}